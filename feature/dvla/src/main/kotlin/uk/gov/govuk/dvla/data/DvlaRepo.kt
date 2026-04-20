package uk.gov.govuk.dvla.data

import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.map
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dvla.domain.CustomerSummaryDetails
import uk.gov.govuk.dvla.domain.DriverSummaryDetails
import uk.gov.govuk.dvla.domain.LicenceDetails
import uk.gov.govuk.dvla.domain.toDomainModel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
internal class DvlaRepo @Inject constructor(
    private val api: DvlaApi,
    private val authRepo: AuthRepo
) {
    private val _isLinked = MutableStateFlow(false)
    val isLinked = _isLinked.asStateFlow()

    suspend fun isAccountLinked(): Result<Boolean> {
        val result = safeAuthApiCall({ api.checkDvlaLinked() }, authRepo)

        return if (result is Result.Success) {
            val linked = result.value.linked
            _isLinked.value = linked
            Result.Success(linked)
        } else {
            @Suppress("UNCHECKED_CAST")
            result as Result<Boolean>
        }
    }

    suspend fun linkAccount(token: String): Result<Unit> {
        val result = try {
            val linkingId = extractLinkingIdFromJwt(token)
            safeAuthApiCall({ api.linkDvlaIdentity(linkingId) }, authRepo)
        } catch (_: Exception) {
            Result.Error()
        }

        _isLinked.value = result is Result.Success
        return result
    }

    suspend fun unlinkAccount(): Result<Unit> {
        val result = safeAuthApiCall({ api.deleteDvlaIdentity() }, authRepo)
        _isLinked.value = result !is Result.Success
        return result
    }

    suspend fun getLicenceDetails(): Result<LicenceDetails> =
        safeAuthApiCall({ api.getDrivingLicence() }, authRepo)
            .map { it.toDomainModel() }

    suspend fun getDriverSummary(): Result<DriverSummaryDetails> =
        safeAuthApiCall({ api.getDriverSummary() }, authRepo)
            .map { it.toDomainModel() }

    suspend fun getCustomerSummary(): Result<CustomerSummaryDetails> =
        safeAuthApiCall({ api.getCustomerSummary() }, authRepo)
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
