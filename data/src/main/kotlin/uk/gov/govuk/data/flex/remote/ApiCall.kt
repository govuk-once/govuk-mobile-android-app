package uk.gov.govuk.data.flex.remote

import retrofit2.Response
import uk.gov.govuk.data.flex.FlexResult
import uk.gov.govuk.data.flex.FlexResult.Success
import uk.gov.govuk.data.flex.FlexResult.Error

internal suspend fun <T> safeFlexApiCall(
    apiCall: suspend () -> Response<T>
): FlexResult<T> {
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
