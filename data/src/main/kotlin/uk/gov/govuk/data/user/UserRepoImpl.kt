package uk.gov.govuk.data.user

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.safeApiCall
import uk.gov.govuk.data.user.remote.UserApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.data.user.model.Preferences
import uk.gov.govuk.data.user.model.UpdateTermsAndConditionsRequest
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest
import uk.gov.govuk.data.user.model.UpdateUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepoImpl @Inject constructor(
    private val userApi: UserApi,
    private val authRepo: AuthRepo
) : UserRepo {

    private var _user: User? = null
    private val safeUser: User
        get() = checkNotNull(_user) { "You must init user successfully before use!!!" }

    override suspend fun initUser(): Result<Unit> {
        val result = safeApiCall(apiCall = { userApi.getUserInfo() })
        return if (result is Success) {
            _user = result.value
            Success(Unit)
        } else {
            @Suppress("UNCHECKED_CAST") // we know it's not a success
            result as Result<Unit>
        }
    }

    override suspend fun updateNotifications(
        consentStatus: ConsentStatus
    ): Result<UpdateUser> {
        return safeAuthApiCall(apiCall = {
            userApi.updateNotifications(
                UpdateNotificationsRequest(consentStatus)
            )
        }, authRepo = authRepo)
    }

    override suspend fun updateTermsAndConditions(
        consentStatus: ConsentStatus
    ): Result<UpdateUser> {
        return safeAuthApiCall(apiCall = {
            userApi.updateTermsAndConditions(
                UpdateTermsAndConditionsRequest(consentStatus)
            )
        }, authRepo = authRepo)
    }

    override val notificationId: String
        get() = safeUser.notificationId

    override val preferences: Preferences
        get() = safeUser.preferences
}
