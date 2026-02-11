package uk.gov.govuk.data.remote

import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result

sealed class AuthApiResult<T> {
    data class Success<T>(val response: Response<T>) : AuthApiResult<T>()
    class AuthError<T> : AuthApiResult<T>()
    class NetworkError<T> : AuthApiResult<T>()
}

suspend fun <T> authenticatedApiCall(
    apiCall: suspend () -> Response<T>,
    authRepo: AuthRepo,
    retry: Boolean = true
): AuthApiResult<T> {
    return try {
        val response = apiCall()
        val code = response.code()

        when (code) {
            401, 403 -> {
                if (retry && authRepo.refreshTokens()) {
                    authenticatedApiCall(apiCall, authRepo, retry = false)
                } else {
                    AuthApiResult.AuthError()
                }
            }
            else -> AuthApiResult.Success(response)
        }
    } catch (e: Exception) {
        AuthApiResult.NetworkError()
    }
}

suspend fun <T> safeAuthApiCall(
    apiCall: suspend () -> Response<T>,
    authRepo: AuthRepo
): Result<T> {
    return when (val result = authenticatedApiCall(apiCall, authRepo)) {
        is AuthApiResult.Success -> {
            val body = result.response.body()
            if (result.response.isSuccessful && body != null) {
                Result.Success(body)
            } else {
                Result.Error()
            }
        }
        is AuthApiResult.AuthError -> Result.AuthError()
        is AuthApiResult.NetworkError -> Result.DeviceOffline()
    }
}
