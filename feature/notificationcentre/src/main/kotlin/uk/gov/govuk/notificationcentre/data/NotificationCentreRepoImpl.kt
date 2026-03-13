package uk.gov.govuk.notificationcentre.data

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.notificationcentre.data.model.Notification
import uk.gov.govuk.notificationcentre.data.model.UpdateNotificationRequestBody
import uk.gov.govuk.notificationcentre.data.remote.NotificationCentreApi
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

class DateProviderImpl: DateProvider {
    override val date: Instant
        get() = Instant.now()
}

@Singleton
class NotificationCentreRepoImpl @Inject constructor(
    private val notificationCentreApi: NotificationCentreApi,
    private val authRepo: AuthRepo,
    private val dateProvider: DateProvider
) : NotificationCentreRepo {

    data class CacheEntry<T>(val value: T, val lastUpdated: Instant = Instant.now()) {
        fun hasExpired(now: Instant): Boolean =
            now.isAfter(lastUpdated.plus(30, ChronoUnit.SECONDS))
    }

    private var notifications: CacheEntry<List<Notification>>? = null

    override suspend fun getNotifications(): Result<List<Notification>> {
        val currNotifications = notifications

        if (currNotifications != null && !currNotifications.hasExpired(dateProvider.date)) {
            return Success(currNotifications.value)
        }

        val res = safeAuthApiCall(apiCall = {
            notificationCentreApi.getNotifications()
        }, authRepo = authRepo)

        if (res is Success) {
            notifications = CacheEntry(res.value)
        }

        return res
    }

    override suspend fun getSingleNotification(notificationId: String): Result<Notification?> {
        if (notifications?.hasExpired(dateProvider.date) == false) {
            notifications?.value?.firstOrNull { notification -> notification.id == notificationId }
                ?.apply {
                    return Success(this)
                }
        }

        val response =
            safeAuthApiCall({ notificationCentreApi.getSingleNotification(notificationId) }, authRepo)

        return if (response is ServiceNotResponding && response.code == 404) {
            Success(null)
        } else {
            response
        }
    }

    override suspend fun updateNotification(notificationId: String, status: UpdateNotificationRequestBody.Status): Result<Unit> {
        notifications?.let { nots ->
            val updatedNotifications = nots.value.map {
                if (it.id == notificationId) {
                    val statusString = when (status) {
                        UpdateNotificationRequestBody.Status.READ -> "READ"
                        UpdateNotificationRequestBody.Status.UNREAD -> "DELIVERED"
                    }

                    it.copy(status = statusString)
                } else {
                    it
                }
            }
            notifications = notifications?.copy(value = updatedNotifications)
        }

        return safeAuthApiCall(apiCall = {
            notificationCentreApi.updateNotification(
                notificationId,
                UpdateNotificationRequestBody(status)
            )
        }, authRepo = authRepo)
    }

    override suspend fun deleteNotification(notificationId: String): Result<Unit> {
        notifications?.let { nots ->
            val updatedNotifications = nots.value.filter { it.id != notificationId }
            notifications = nots.copy(value = updatedNotifications)
        }

        return safeAuthApiCall(apiCall = {
            notificationCentreApi.deleteNotification(
                notificationId
            )
        }, authRepo = authRepo)
    }
}
