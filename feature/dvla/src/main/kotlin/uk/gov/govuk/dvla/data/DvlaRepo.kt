package uk.gov.govuk.dvla.data

import com.google.gson.JsonParser
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeAuthApiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
internal class DvlaRepo @Inject constructor(
    private val api: DvlaApi,
    private val authRepo: AuthRepo
) {
    var isLinked = false
        private set

    suspend fun isAccountLinked(): Result<Boolean> = safeAuthApiCall({ api.checkDvlaLinked() }, authRepo)

    suspend fun linkAccount(token: String): Result<Unit> {
        val result = try {
            val linkingId = extractLinkingIdFromJwt(token)
            safeAuthApiCall({ api.linkDvlaIdentity(linkingId) }, authRepo)
        } catch (e: Exception) {
            Result.Error()
        }

        isLinked = result is Result.Success
        return result
    }

    suspend fun unlinkAccount(): Result<Unit> {
        val result = safeAuthApiCall({ api.deleteDvlaIdentity() }, authRepo)
        isLinked = result !is Result.Success
        return result
    }

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