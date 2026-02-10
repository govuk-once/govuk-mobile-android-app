package uk.gov.govuk.data.remote

import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    retry: suspend (code: Int) -> Boolean = { false },
    apiCall: suspend () -> Response<T>
): Result<T> {
    return try {
        val response = apiCall()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Success(body)
        } else {
            if (retry(response.code())) {
                safeApiCall(apiCall = apiCall)
            } else {
                Error()
            }
        }
    } catch (e: Exception) {
        when (e) {
            is UnknownHostException -> DeviceOffline()
            is HttpException -> ServiceNotResponding()
            else -> Error()
        }
    }
}
