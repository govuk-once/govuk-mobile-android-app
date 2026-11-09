package uk.gov.govuk.data.user

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User

interface UserRepo {
    val notifications: Notifications?

    suspend fun initUser(): Result<User>
    suspend fun updateNotifications(consentStatus: ConsentStatus): Result<Unit>
}
