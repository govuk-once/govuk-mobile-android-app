package uk.gov.govuk.data.user

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Preferences
import uk.gov.govuk.data.user.model.UpdateUserDataResponse

interface UserRepo {
    val notificationId: String
    val preferences: Preferences

    suspend fun initUser(): Result<Unit>
    suspend fun updateNotifications(consentStatus: ConsentStatus): Result<UpdateUserDataResponse>
}
