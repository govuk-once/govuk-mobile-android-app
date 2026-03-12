package uk.gov.govuk.data.user

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications

interface UserRepo {
    val notifications: Notifications?

    suspend fun initUser(): Result<Unit>
    suspend fun updateNotifications(consentStatus: ConsentStatus): Result<Unit>
}
