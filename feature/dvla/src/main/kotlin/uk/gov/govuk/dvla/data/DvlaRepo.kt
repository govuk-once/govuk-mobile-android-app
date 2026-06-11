package uk.gov.govuk.dvla.data

import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.identity.IdentityRepo
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.map
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.data.local.DvlaDataStore
import uk.gov.govuk.dvla.domain.CustomerSummary
import uk.gov.govuk.dvla.domain.DriverSummary
import uk.gov.govuk.dvla.domain.LicenceDetails
import uk.gov.govuk.dvla.domain.CheckCodeDetails
import uk.gov.govuk.dvla.domain.VesVehicle
import uk.gov.govuk.dvla.domain.toDomainModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DvlaRepo @Inject constructor(
    private val api: DvlaApi,
    private val authRepo: AuthRepo,
    private val dvlaDataStore: DvlaDataStore,
    private val identityRepo: IdentityRepo
) {

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
            // sync linked services state
            identityRepo.getLinkedServices()
        }
        return result
    }

    internal suspend fun getLicenceDetails(): Result<LicenceDetails> =
        safeAuthApiCall({ api.getDrivingLicence() }, authRepo)
            .map { it.toDomainModel() }

    internal suspend fun getDriverSummary(): Result<DriverSummary> =
        safeAuthApiCall({ api.getDriverSummary() }, authRepo)
            .map { it.toDomainModel() }

    internal suspend fun getCustomerSummary(): Result<CustomerSummary> =
        safeAuthApiCall({ api.getCustomerSummary() }, authRepo)
            .map { it.toDomainModel() }

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
