package uk.gov.govuk.data.user

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.safeApiCall
import uk.gov.govuk.data.user.remote.UserApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepoImpl @Inject constructor(
    private val userApi: UserApi,
    private val authRepo: AuthRepo
) : UserRepo {

    private var user: User? = null

    override val notifications: Notifications?
        get() = user?.notifications

    override suspend fun initUser(): Result<User> {
        val result = safeApiCall(apiCall = { userApi.getUserInfo() })
        if (result is Success) {
            user = result.value
        }
        return result
    }

    override suspend fun updateNotifications(
        consentStatus: ConsentStatus
    ) = safeAuthApiCall(apiCall = {
        userApi.updateNotifications(
            UpdateNotificationsRequest(consentStatus)
        )
    }, authRepo = authRepo)
}
