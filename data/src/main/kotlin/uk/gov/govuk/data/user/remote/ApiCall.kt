package uk.gov.govuk.data.user.remote

import retrofit2.Response
import uk.gov.govuk.data.user.UserApiResult
import uk.gov.govuk.data.user.UserApiResult.Success
import uk.gov.govuk.data.user.UserApiResult.Error

internal suspend fun <T> safeUserApiCall(
    apiCall: suspend () -> Response<T>
): UserApiResult<T> {
    return try {
        val response = apiCall()
        val body = response.body()
        when {
            response.isSuccessful -> {
                when {
                    body != null -> Success(body)
                    else -> Error()
                }
            }

            else -> Error()
        }
    } catch (_: Exception) {
        Error()
    }
}
