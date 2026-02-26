package uk.gov.govuk.chat.data.remote

import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.AuthenticationException
import uk.gov.govuk.data.remote.withAuthRetry

internal suspend fun <T> safeChatApiCall(
    apiCall: suspend () -> Response<T>,
    authRepo: AuthRepo
): ChatResult<T> {
    return try {
        val response = withAuthRetry(apiCall, authRepo)
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
    } catch (e: AuthenticationException) {
        ChatResult.AuthError()
    } catch (e: Exception) {
        ChatResult.DeviceOffline()
    }
}
