package uk.gov.govuk.dvla.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.map
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.data.local.DvlaDataStore
import uk.gov.govuk.dvla.domain.CustomerSummary
import uk.gov.govuk.dvla.domain.DriverSummary
import uk.gov.govuk.dvla.domain.DvlaLinkState
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
    private val dvlaDataStore: DvlaDataStore
) {
    private val _linkState = MutableStateFlow(DvlaLinkState.CHECKING)
    val linkState = _linkState.asStateFlow()

    internal suspend fun getSelectedDrivingView(): DrivingView? = dvlaDataStore.getSelectedDrivingView()

    internal suspend fun setSelectedDrivingView(drivingView: DrivingView) = dvlaDataStore.setSelectedDrivingView(drivingView)

    suspend fun clear() {
        dvlaDataStore.clear()
    }

    suspend fun isAccountLinked(): Result<Boolean> {
        val result = safeAuthApiCall({ api.checkDvlaLinked() }, authRepo)

        return if (result is Result.Success) {
            val linked = result.value.linked
            _linkState.value = if (linked) DvlaLinkState.LINKED else DvlaLinkState.UNLINKED
            Result.Success(linked)
        } else {
            _linkState.value = DvlaLinkState.UNLINKED

            @Suppress("UNCHECKED_CAST")
            result as Result<Boolean>
        }
    }

    internal suspend fun linkAccount(token: String): Result<Unit> {
        val result = try {
            safeAuthApiCall({ api.linkDvlaIdentity(token) }, authRepo)
        } catch (_: Exception) {
            Result.Error()
        }

        if (result is Result.Success) {
            _linkState.value = DvlaLinkState.LINKED
        }
        return result
    }

    suspend fun unlinkAccount(): Result<Unit> {
        val result = safeAuthApiCall({ api.deleteDvlaIdentity() }, authRepo)
        if (result is Result.Success) {
            _linkState.value = DvlaLinkState.UNLINKED
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
