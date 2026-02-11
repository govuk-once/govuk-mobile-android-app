package uk.gov.govuk.data.remote

import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result

class AuthenticationException : Exception()

suspend fun <T> withAuthRetry(
    apiCall: suspend () -> Response<T>,
    authRepo: AuthRepo,
    retry: Boolean = true
): Response<T> {
    val response = apiCall()

    return when (response.code()) {
        401, 403 -> {
            if (retry && authRepo.refreshTokens()) {
                withAuthRetry(apiCall, authRepo, retry = false)
            } else {
                throw AuthenticationException()
            }
        }
        else -> response
    }
}

suspend fun <T> safeAuthApiCall(
    apiCall: suspend () -> Response<T>,
    authRepo: AuthRepo
): Result<T> {
    return safeApiCall { withAuthRetry(apiCall, authRepo) }
}
