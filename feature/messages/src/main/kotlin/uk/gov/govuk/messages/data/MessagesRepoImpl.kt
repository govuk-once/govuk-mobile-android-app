package uk.gov.govuk.messages.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.messages.data.di.MessagesScope
import uk.gov.govuk.messages.data.model.Message
import uk.gov.govuk.messages.data.model.Notification
import uk.gov.govuk.messages.data.model.UpdateNotificationRequestBody
import uk.gov.govuk.messages.data.remote.NotificationsApi
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

class DateProviderImpl: DateProvider {
    override val date: Instant
        get() = Instant.now()
}

@Singleton
internal class MessagesRepoImpl @Inject constructor(
    private val notificationsApi: NotificationsApi,
    private val authRepo: AuthRepo,
    private val dateProvider: DateProvider,
    @param:MessagesScope private val scope: CoroutineScope
) : MessagesRepo {

    data class CacheEntry<T>(val value: T, val lastUpdated: Instant = Instant.now()) {
        fun hasExpired(now: Instant): Boolean =
            now.isAfter(lastUpdated.plus(30, ChronoUnit.SECONDS))
    }

    private var notifications: CacheEntry<List<Message>>? = null

    override suspend fun getMessages(): Result<List<Message>> {
        val currNotifications = notifications

        if (currNotifications != null && !currNotifications.hasExpired(dateProvider.date)) {
            return Success(currNotifications.value)
        }

        val res = safeAuthApiCall(apiCall = {
            notificationsApi.getNotifications()
        }, authRepo = authRepo)

        if (res is Success) {
            val curr = notifications

            // Discard the fetch value if a mutation extended the expiry of the cached value
            // while this call was in flight
            val entry = if (curr == null || curr.hasExpired(dateProvider.date)) {
                CacheEntry(res.value)
            } else {
                curr
            }
            notifications = entry

            return Success(entry.value)
        }

        return res
    }

    override suspend fun getSingleMessage(messageId: String): Result<Message?> {
        if (notifications?.hasExpired(dateProvider.date) == false) {
            notifications?.value?.firstOrNull { notification -> notification.id == messageId }
                ?.apply {
                    return Success(this)
                }
        }

        val response =
            safeAuthApiCall({ notificationsApi.getSingleNotification(messageId) }, authRepo)

        return if (response is ServiceNotResponding && response.code == 404) {
            Success(null)
        } else {
            response
        }
    }

    override suspend fun updateMessage(messageId: String, status: UpdateNotificationRequestBody.Status): Result<Unit> {
        notifications = notifications?.let { nots ->
            val statusString = when (status) {
                UpdateNotificationRequestBody.Status.READ -> "READ"
                UpdateNotificationRequestBody.Status.UNREAD -> "DELIVERED"
            }

            CacheEntry(nots.value.map {
                if (it.id == messageId) it.copy(status = statusString) else it
            })
        }

        scope.launch {
            safeAuthApiCall(apiCall = {
                notificationsApi.updateNotification(
                    messageId,
                    UpdateNotificationRequestBody(status)
                )
            }, authRepo = authRepo)
        }

        return Success(Unit)
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        notifications = notifications?.let { nots ->
            CacheEntry(nots.value.filter { it.id != messageId })
        }

        scope.launch {
            safeAuthApiCall(apiCall = {
                notificationsApi.deleteNotification(
                    messageId
                )
            }, authRepo = authRepo)
        }

        return Success(Unit)
    }
}
