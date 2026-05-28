package uk.gov.govuk.dvla.data

import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.map
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dvla.ui.model.Category
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
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
class DvlaRepo @Inject constructor(
    private val api: DvlaApi,
    private val authRepo: AuthRepo,
    private val dvlaDataStore: DvlaDataStore
) {
    private val _linkState = MutableStateFlow(DvlaLinkState.CHECKING)
    val linkState = _linkState.asStateFlow()

    internal suspend fun getSelectedCategory(): Category? = dvlaDataStore.getSelectedCategory()

    internal suspend fun setSelectedCategory(category: Category) = dvlaDataStore.setSelectedCategory(category)

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
            val linkingId = extractLinkingIdFromJwt(token)
            safeAuthApiCall({ api.linkDvlaIdentity(linkingId) }, authRepo)
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

    @OptIn(ExperimentalEncodingApi::class)
    private fun extractLinkingIdFromJwt(jwtToken: String): String {
        return try {
            val parts = jwtToken.split(".")
            require(parts.size >= 2) { "Invalid JWT" }

            val payloadBase64 = parts[1]
            val decodedBytes = Base64.UrlSafe
                .withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
                .decode(payloadBase64)
            val decodedString = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JsonParser.parseString(decodedString).asJsonObject
            jsonObject["linking_id"].asString

        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to extract linking id", e)
        }
    }
}
