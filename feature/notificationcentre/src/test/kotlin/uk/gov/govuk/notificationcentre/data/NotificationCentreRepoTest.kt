package uk.gov.govuk.notificationcentre.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notificationcentre.data.model.Notification
import uk.gov.govuk.notificationcentre.data.model.UpdateNotificationRequestBody
import uk.gov.govuk.notificationcentre.data.remote.NotificationCentreApi
import uk.gov.govuk.notificationcentre.fixtures.NotificationCentreFixtures.Companion.mockNotifications
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationCentreRepoTest {

    private val api = mockk<NotificationCentreApi>(relaxed = true)
    private val auth = mockk<AuthRepo>(relaxed = true)

    private var mockDateProvider = mockk<DateProvider>()

    private val getAllResponse = mockk<Response<List<Notification>>>(relaxed = true)
    private val getSingleResponse = mockk<Response<Notification?>>(relaxed = true)

    private lateinit var notificationCentreRepo: NotificationCentreRepo

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        every { mockDateProvider.date } returns Instant.now()

        notificationCentreRepo = NotificationCentreRepoImpl(
            api,
            auth,
            mockDateProvider,
            CoroutineScope(UnconfinedTestDispatcher())
        )
    }

    // All

    @Test
    fun `Get notifications performs API call when not cached`() = runTest {
        notificationCentreRepo.getNotifications()

        coVerify {
            api.getNotifications()
        }
    }

    @Test
    fun `Get notifications performs API call only once when cached`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        notificationCentreRepo.getNotifications()
        notificationCentreRepo.getNotifications()

        coVerify(exactly = 1) {
            api.getNotifications()
        }
    }

    @Test
    fun `Get notifications performs API call when cache has expired`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        notificationCentreRepo.getNotifications()

        coVerify(exactly = 1) {
            api.getNotifications()
        }

        every { mockDateProvider.date } returns Instant.now().plus(60,ChronoUnit.SECONDS)
        notificationCentreRepo.getNotifications()

        coVerify(exactly = 2) {
            api.getNotifications()
        }
    }

    // Single

    @Test
    fun `Get single notification uses cache`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        // Warm the cache
        notificationCentreRepo.getNotifications()

        notificationCentreRepo.getSingleNotification("1")

        coVerify(exactly = 1) {
            api.getNotifications()
        }

        coVerify(exactly = 0) {
            api.getSingleNotification(any())
        }
    }

    @Test
    fun `Get single notification calls API when cache miss`() = runTest {
        coEvery { api.getSingleNotification("1") } returns getSingleResponse
        coEvery { getSingleResponse.isSuccessful } returns true
        coEvery { getSingleResponse.body() } returns mockNotifications.first()

        notificationCentreRepo.getSingleNotification("1")

        coVerify(exactly = 0) {
            api.getNotifications()
        }

        coVerify(exactly = 1) {
            api.getSingleNotification("1")
        }
    }

    @Test
    fun `Get single notification falls through to API when id not present in warm cache`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        // Warm the cache but the GET will avoid it as the ID isn't in it so is forced to hit the network
        notificationCentreRepo.getNotifications()

        notificationCentreRepo.getSingleNotification("999")

        coVerify(exactly = 1) {
            api.getSingleNotification("999")
        }
    }

    @Test
    fun `Get single notification calls API when cache stale`() = runTest {
        coEvery { api.getSingleNotification("1") } returns getSingleResponse
        coEvery { getSingleResponse.isSuccessful } returns true
        coEvery { getSingleResponse.body() } returns mockNotifications.first()

        // Warm cache
        notificationCentreRepo.getNotifications()
        every { mockDateProvider.date } returns Instant.now().plus(60,ChronoUnit.SECONDS)

        notificationCentreRepo.getSingleNotification("1")

        coVerify(exactly = 1) {
            api.getNotifications()
        }

        coVerify(exactly = 1) {
            api.getSingleNotification("1")
        }
    }

    @Test
    fun `Get single notification returns null when not found`() = runTest {
        coEvery { api.getSingleNotification("1") } returns getSingleResponse
        coEvery { getSingleResponse.code() } returns 404
        coEvery { getSingleResponse.body() } returns null

        val res = notificationCentreRepo.getSingleNotification("1")

        assertTrue(res is Result.Success)
        assertNull(res.value)

        coVerify(exactly = 0) {
            api.getNotifications()
        }

        coVerify(exactly = 1) {
            api.getSingleNotification("1")
        }
    }

    @Test
    fun `Get single notification returns error from API`() = runTest {
        coEvery { api.getSingleNotification("1") } returns getSingleResponse
        coEvery { getSingleResponse.body() } returns null

        val res = notificationCentreRepo.getSingleNotification("1")

        assertTrue(res is Result.ServiceNotResponding)

        coVerify(exactly = 0) {
            api.getNotifications()
        }

        coVerify(exactly = 1) {
            api.getSingleNotification("1")
        }
    }

    // Update

    @Test
    fun `Update notification makes API call for read`() = runTest {
        notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.READ)


        coVerify(exactly = 1) {
            api.updateNotification("1", UpdateNotificationRequestBody(UpdateNotificationRequestBody.Status.READ))
        }
    }

    @Test
    fun `Update notification makes API call for unread`() = runTest {
        notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.UNREAD)


        coVerify(exactly = 1) {
            api.updateNotification("1", UpdateNotificationRequestBody(UpdateNotificationRequestBody.Status.UNREAD))
        }
    }

    @Test
    fun `Mark read modifies internal state`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        // Prime cache
        notificationCentreRepo.getNotifications()

        notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.READ)

        val notification = notificationCentreRepo.getSingleNotification("1")

        assertEquals((notification as Result.Success).value?.isUnread, false)
    }

    @Test
    fun `Mark unread modifies internal state`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        // Prime cache
        notificationCentreRepo.getNotifications()

        notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.READ)
        var notification = notificationCentreRepo.getSingleNotification("1")
        assertEquals((notification as Result.Success).value?.isUnread, false)

        notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.UNREAD)

        notification = notificationCentreRepo.getSingleNotification("1")
        assertEquals((notification as Result.Success).value?.isUnread, true)
    }

    // Delete

    @Test
    fun `Delete notification makes API call for read`() = runTest {
        notificationCentreRepo.deleteNotification("1")


        coVerify(exactly = 1) {
            api.deleteNotification("1")
        }
    }

    @Test
    fun `Delete notification modifies internal state`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns listOf(mockNotifications.first())

        // Prime cache
        notificationCentreRepo.getNotifications()

        notificationCentreRepo.deleteNotification("1")

        // Using `getNotifications` means we don't have to mock the API call that `getSingle` would hit
        val notifications = notificationCentreRepo.getNotifications()

        assertTrue((notifications as Result.Success).value.isEmpty())
    }

    // NOT-417

    @Test
    fun `Get notifications does not clobber a status update mutated while its network call was in flight`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        // Prime cache
        notificationCentreRepo.getNotifications()

        // Force the cache to immediately expire, and then when the mutation happens, set it back to now
        every { mockDateProvider.date } returnsMany listOf(
            Instant.now().plus(60, ChronoUnit.SECONDS),
            Instant.now()
        )

        // Force a mark as unread to happen before the GET finishes
        coEvery { api.getNotifications() } coAnswers {
            notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.READ)
            getAllResponse
        }

        val result = notificationCentreRepo.getNotifications()

        // The deleted value (even with the stale GET) has still been removed
        assertEquals(false, (result as Result.Success).value.first { it.id == "1" }.isUnread)
    }

    @Test
    fun `Get notifications does not clobber a cache mutated while its network call was in flight`() = runTest {
        coEvery { api.getNotifications() } returns getAllResponse
        coEvery { getAllResponse.isSuccessful } returns true
        coEvery { getAllResponse.body() } returns mockNotifications

        // Prime cache
        notificationCentreRepo.getNotifications()

        // Force the cache to immediately expire, and then when the mutation happens, set it back to now
        every { mockDateProvider.date } returnsMany listOf(
            Instant.now().plus(60, ChronoUnit.SECONDS),
            Instant.now()
        )

        // Force a delete to happen before the GET finishes
        coEvery { api.getNotifications() } coAnswers {
            notificationCentreRepo.deleteNotification("1")
            getAllResponse
        }

        val result = notificationCentreRepo.getNotifications()

        // The deleted value (even with the stale GET) has still been removed
        assertTrue((result as Result.Success).value.none { it.id == "1" })
    }

    @Test
    fun `Delete network call survives cancellation of the calling coroutine`() = runTest {
        // Move to our own Scope to mirror how the ViewModel behaves
        val callerJob = Job()
        val callerScope = CoroutineScope(UnconfinedTestDispatcher() + callerJob)

        callerScope.launch {
            notificationCentreRepo.deleteNotification("1")
        }
        callerJob.cancel()

        advanceUntilIdle()

        coVerify(exactly = 1) {
            api.deleteNotification("1")
        }
    }
}