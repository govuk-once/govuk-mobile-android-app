package uk.gov.govuk.data.user

import uk.gov.govuk.data.user.model.UserApiResponse
import uk.gov.govuk.data.user.remote.UserApi
import uk.gov.govuk.data.user.remote.safeUserApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getUserPreferences(): UserApiResult<UserApiResponse> {
        return safeUserApiCall(apiCall = { userApi.getUserPreferences() })
    }
}
