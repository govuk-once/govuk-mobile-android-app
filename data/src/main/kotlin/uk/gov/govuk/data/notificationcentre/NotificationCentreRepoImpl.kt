package uk.gov.govuk.data.notificationcentre

import retrofit2.HttpException
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.AuthError
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.data.notificationcentre.model.UpdateNotificationRequestBody
import uk.gov.govuk.data.notificationcentre.remote.NotificationCentreApi
import uk.gov.govuk.data.remote.AuthenticationException
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.data.remote.withAuthRetry
import java.net.UnknownHostException
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class NotificationCentreRepoImpl @Inject constructor(
    private val notificationCentreApi: NotificationCentreApi,
    private val authRepo: AuthRepo
) : NotificationCentreRepo {

    data class CacheEntry<T>(val value: T, val lastUpdated: Instant = Instant.now()) {
        val hasExpired: Boolean
            get() = Instant.now().isAfter(lastUpdated.plus(30, ChronoUnit.SECONDS))
    }

    private var notifications: CacheEntry<List<Notification>>? = null

    override suspend fun getNotifications(): Result<List<Notification>> {
        val currNotifications = notifications
        if (currNotifications != null && !currNotifications.hasExpired) {
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
        return try {
            val response = withAuthRetry( {notificationCentreApi.getSingleNotification(notificationId) }, authRepo)
            val body = response.body()
            return if (response.isSuccessful && body != null) {
                Success(body)
            } else {
                if (response.code() == 404) {
                    Success(null)
                } else {
                    Error()
                }
            }
        } catch (e: Exception) {
            when (e) {
                is AuthenticationException -> AuthError()
                is UnknownHostException -> DeviceOffline()
                is HttpException -> ServiceNotResponding()
                else -> Error()
            }
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
            notifications = notifications?.copy(value = updatedNotifications)
        }

        return safeAuthApiCall(apiCall = {
            notificationCentreApi.deleteNotification(
                notificationId
            )
        }, authRepo = authRepo)
    }
}
