package uk.gov.govuk.chat.data.remote

import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.AuthApiResult
import uk.gov.govuk.data.remote.authenticatedApiCall

internal suspend fun <T> safeChatApiCall(
    apiCall: suspend () -> Response<T>,
    authRepo: AuthRepo
): ChatResult<T> {
    return when (val result = authenticatedApiCall(apiCall, authRepo)) {
        is AuthApiResult.Success -> {
            val response = result.response
            val body = response.body()
            val code = response.code()

            when {
                response.isSuccessful -> {
                    when {
                        code == 202 -> ChatResult.AwaitingAnswer()
                        body != null -> ChatResult.Success(body)
                        else -> ChatResult.Error()
                    }
                }
                else -> {
                    when (code) {
                        404 -> ChatResult.NotFound()
                        422 -> ChatResult.ValidationError()
                        429 -> ChatResult.RateLimitExceeded()
                        else -> ChatResult.Error()
                    }
                }
            }
        }
        is AuthApiResult.AuthError -> ChatResult.AuthError()
        is AuthApiResult.NetworkError -> ChatResult.DeviceOffline()
    }
}
