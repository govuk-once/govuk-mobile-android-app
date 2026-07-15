package uk.gov.govuk.dvla.remote

import com.google.gson.Gson
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.AuthenticationException
import uk.gov.govuk.data.remote.withAuthRetry
import uk.gov.govuk.dvla.domain.LicenceDetailsResult
import uk.gov.govuk.dvla.domain.toDomainModel
import uk.gov.govuk.dvla.remote.model.DvlaErrorResponse
import uk.gov.govuk.dvla.remote.model.LicenceResponse

private const val ERROR_CODE_LICENCE_NOT_FOUND = "GUK-404-04"
private const val ERROR_CODE_LICENCE_NOT_AVAILABLE_FOR_ENQUIRY = "GUK-404-05"

private val gson = Gson()

internal suspend fun safeLicenceApiCall(
    apiCall: suspend () -> Response<LicenceResponse>,
    authRepo: AuthRepo
): LicenceDetailsResult {
    return try {
        val response = withAuthRetry(apiCall, authRepo)
        val body = response.body()
        val code = response.code()

        when {
            response.isSuccessful -> {
                if (body != null) {
                    LicenceDetailsResult.Success(body.toDomainModel())
                } else {
                    LicenceDetailsResult.Failure(Result.Error())
                }
            }

            code == 404 -> when (parseDvlaErrorCode(response)) {
                ERROR_CODE_LICENCE_NOT_FOUND -> LicenceDetailsResult.NotFound
                ERROR_CODE_LICENCE_NOT_AVAILABLE_FOR_ENQUIRY -> LicenceDetailsResult.NotAvailableForEnquiry
                else -> LicenceDetailsResult.Failure(Result.ServiceNotResponding(code))
            }

            else -> LicenceDetailsResult.Failure(Result.ServiceNotResponding(code))
        }
    } catch (e: AuthenticationException) {
        LicenceDetailsResult.Failure(Result.AuthError())
    } catch (e: Exception) {
        LicenceDetailsResult.Failure(Result.DeviceOffline())
    }
}

private fun parseDvlaErrorCode(response: Response<LicenceResponse>): String? {
    val errorBody = response.errorBody() ?: return null
    return runCatching { gson.fromJson(errorBody.charStream(), DvlaErrorResponse::class.java)?.error?.code }.getOrNull()
}
