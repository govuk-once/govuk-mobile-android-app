package uk.gov.govuk.dvla.data

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.identity.IdentityRepo
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.map
import uk.gov.govuk.data.remote.AuthenticationException
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.data.remote.withAuthRetry
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.data.local.DvlaDataStore
import uk.gov.govuk.dvla.domain.LicenceDetails
import uk.gov.govuk.dvla.domain.LicenceDetailsResult
import uk.gov.govuk.dvla.domain.CheckCodeDetails
import uk.gov.govuk.dvla.domain.VehicleDetails
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.domain.VesVehicle
import uk.gov.govuk.dvla.domain.toDomainModel
import uk.gov.govuk.dvla.remote.model.DvlaErrorBody
import java.io.Reader
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

private const val ERROR_CODE_LICENCE_NOT_FOUND = "GUK-404-04"
private const val ERROR_CODE_LICENCE_NOT_AVAILABLE_FOR_ENQUIRY = "GUK-404-05"

@Singleton
class DvlaRepo @Inject constructor(
    private val api: DvlaApi,
    private val authRepo: AuthRepo,
    private val dvlaDataStore: DvlaDataStore,
    private val identityRepo: IdentityRepo
) {
    private val gson = Gson()

    val linkState: Flow<ServiceLinkStatus> = identityRepo.linkStatusOf(LinkedService.DVLA)

    val currentLinkState: ServiceLinkStatus
        get() = identityRepo.currentStatusOf(LinkedService.DVLA)

    suspend fun refreshLinkStatus() = identityRepo.getLinkedServices()

    internal suspend fun getSelectedDrivingView(): DrivingView? = dvlaDataStore.getSelectedDrivingView()

    internal suspend fun setSelectedDrivingView(drivingView: DrivingView) = dvlaDataStore.setSelectedDrivingView(drivingView)

    suspend fun clear() {
        dvlaDataStore.clear()
    }

    internal suspend fun linkAccount(token: String): Result<Unit> {
        val result = try {
            safeAuthApiCall({ api.linkDvlaIdentity(token) }, authRepo)
        } catch (_: Exception) {
            Result.Error()
        }

        if (result is Result.Success) {
            // sync linked services state
            identityRepo.getLinkedServices()
        }
        return result
    }

    suspend fun unlinkAccount(): Result<Unit> {
        val result = safeAuthApiCall({ api.deleteDvlaIdentity() }, authRepo)
        if (result is Result.Success) {
            clear()
            identityRepo.getLinkedServices() // sync linked services state
        }
        return result
    }

    internal suspend fun getLicenceDetails(): LicenceDetailsResult {
        val response = try {
            withAuthRetry({ api.getDrivingLicence() }, authRepo)
        } catch (e: AuthenticationException) {
            return LicenceDetailsResult.Failure(Result.AuthError())
        } catch (e: UnknownHostException) {
            return LicenceDetailsResult.Failure(Result.DeviceOffline())
        } catch (e: Exception) {
            return LicenceDetailsResult.Failure(Result.Error())
        }

        if (response.isSuccessful) {
            val body = response.body()
            return if (body != null) {
                LicenceDetailsResult.Success(body.toDomainModel())
            } else {
                LicenceDetailsResult.Failure(Result.Error())
            }
        }

        if (response.code() == 404) {
            when (parseDvlaErrorCode(response.errorBody()?.charStream())) {
                ERROR_CODE_LICENCE_NOT_FOUND -> return LicenceDetailsResult.NotFound
                ERROR_CODE_LICENCE_NOT_AVAILABLE_FOR_ENQUIRY -> return LicenceDetailsResult.NotAvailableForEnquiry
            }
        }

        return LicenceDetailsResult.Failure(Result.ServiceNotResponding(response.code()))
    }

    private fun parseDvlaErrorCode(errorBody: Reader?): String? {
        errorBody ?: return null
        return runCatching { gson.fromJson(errorBody, DvlaErrorBody::class.java)?.code }.getOrNull()
    }

    internal suspend fun getCustomerVehicles(): Result<List<VehicleSummary>> =
        safeAuthApiCall({ api.getCustomerVehicles() }, authRepo)
            .map { it.customerVehicles.map { vehicle -> vehicle.toDomainModel() } }

    internal suspend fun getVehicleDetails(vehicleId: Int): Result<VehicleDetails> =
        safeAuthApiCall({ api.getVehicleDetails(vehicleId) }, authRepo)
            .map { it.customerVehicleDetails.toDomainModel() }

    internal suspend fun lookupVehicle(registrationNumber: String): Result<VesVehicle> =
        safeAuthApiCall({ api.lookupVehicle(registrationNumber) }, authRepo)
            .map { it.toDomainModel() }

    internal suspend fun createCheckCode(): Result<CheckCodeDetails> =
        safeAuthApiCall({ api.createShareCode() }, authRepo)
            .map { it.toDomainModel() }

    internal suspend fun getCheckCodes(): Result<List<CheckCodeDetails>> =
        safeAuthApiCall({ api.getShareCodes() }, authRepo)
            .map { it.toDomainModel() }

    internal suspend fun cancelCheckCode(tokenId: String): Result<CheckCodeDetails> =
        safeAuthApiCall({ api.cancelShareCode(tokenId) }, authRepo)
            .map { it.toDomainModel() }
}
