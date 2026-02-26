package uk.govuk.app.local.data.remote

import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.remote.model.LocalAuthorityResponse
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.Addresses
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.ApiNotResponding
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.DeviceNotConnected
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.InvalidPostcode
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.LocalAuthority
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.PostcodeEmptyOrNull
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.PostcodeNotFound
import java.net.UnknownHostException

internal suspend fun safeLocalApiCall(
    apiCall: suspend () -> Response<LocalAuthorityResponse>
): Result<LocalAuthorityResult> {
    return try {
        val response = apiCall()
        val body = response.body()
        val code = response.code()

        if (response.isSuccessful && body != null) {
            when {
                body.localAuthority != null -> Success(LocalAuthority(body.localAuthority))
                body.addresses != null -> Success(Addresses(body.addresses))
                else -> Error()
            }
        } else {
                when (code) {
                    400 -> Success(InvalidPostcode)
                    404 -> Success(PostcodeNotFound)
                    418 -> Success(PostcodeEmptyOrNull)
                    429 -> Success(ApiNotResponding)
                    else -> Error()
                }
        }
    } catch (e: Exception) {
        when (e) {
            is UnknownHostException -> Success(DeviceNotConnected)
            is HttpException -> Success(ApiNotResponding)
            else -> Error()
        }
    }
}
