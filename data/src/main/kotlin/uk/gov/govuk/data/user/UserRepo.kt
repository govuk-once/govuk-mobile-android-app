package uk.gov.govuk.data.user

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.safeApiCall
import uk.gov.govuk.data.user.model.GetUserInfoResponse
import uk.gov.govuk.data.user.remote.UserApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.model.UpdateAnalyticsRequest
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest
import uk.gov.govuk.data.user.model.UpdateUserDataResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val userApi: UserApi,
    private val authRepo: AuthRepo
) {
    suspend fun getUserInfo(): Result<GetUserInfoResponse> {
        return safeApiCall { userApi.getUserInfo() }
    }

    suspend fun updateNotifications(
        consented: Boolean
    ): Result<UpdateUserDataResponse> {
        return safeApiCall(
            retry = { retry(it) }) {
            userApi.updateNotifications(
                UpdateNotificationsRequest(consented = consented)
            )
        }
    }

    suspend fun updateAnalytics(
        consented: Boolean
    ): Result<UpdateUserDataResponse> {
        return safeApiCall(
            retry = { retry(it) }) {
            userApi.updateAnalytics(
                UpdateAnalyticsRequest(consented = consented)
            )
        }
    }

    suspend fun retry(errorCode: Int) = when (errorCode) {
        401, 403 -> authRepo.refreshTokens()
        else -> false
    }
}
