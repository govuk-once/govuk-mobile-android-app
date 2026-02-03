package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import com.onesignal.OneSignal
import com.onesignal.notifications.INotification
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OneSignalClientTest {
    private val context = mockk<Context>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var notificationsProvider: NotificationsProvider

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        notificationsProvider = OneSignalClient(context)

        mockkStatic(OneSignal::class)
        mockkStatic(OneSignal.Debug::class)
        mockkStatic(Uri::class)
        mockkStatic(NotificationManagerCompat::class)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `Given we have a notifications client, when initialise is called, then One Signal initialise function is called`() {
        val oneSignalAppId = "1234"
        every { OneSignal.initWithContext(context, oneSignalAppId) } returns Unit

        runTest {
            notificationsProvider.initialise(oneSignalAppId)

            verify(exactly = 1) {
                OneSignal.initWithContext(context, oneSignalAppId)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when request permission is called, then One Signal request permission function is called`() {
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns false

        runTest {
            notificationsProvider.requestPermission()

            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when give consent is called, then One Signal consent given is true`() {
        runTest {
            notificationsProvider.giveConsent()

            assertTrue(OneSignal.consentGiven)
        }
    }

    @Test
    fun `Given we have a notifications client, when remove consent is called, then One Signal consent given is false`() {
        runTest {
            notificationsProvider.removeConsent()

            assertFalse(OneSignal.consentGiven)
        }
    }

    @Test
    fun `Given we have a notifications client, when consent given is called and One Signal consent is true, then consent given returns true`() {
        every {OneSignal.consentGiven} returns true

        runTest {
            assertTrue(notificationsProvider.consentGiven())
        }
    }

    @Test
    fun `Given we have a notifications client, when consent given is called and One Signal consent is false, then consent given returns false`() {
        every {OneSignal.consentGiven} returns false

        runTest {
            assertFalse(notificationsProvider.consentGiven())
        }
    }

    @Test
    fun `Given we have a notifications client, when permission granted is called and notifications are disabled, returns false`() {
        every { NotificationManagerCompat.from(context).areNotificationsEnabled() } returns false

        runTest {
            assertFalse(notificationsProvider.permissionGranted())
        }
    }

    @Test
    fun `Given we have a notifications client, when permission granted is called and notifications are enabled, returns true`() {
        every { NotificationManagerCompat.from(context).areNotificationsEnabled() } returns true

        runTest {
            assertTrue(notificationsProvider.permissionGranted())
        }
    }

    @Test
    fun `Given we have a notifications client, when add click listener is called, then the correct functions are called`() {
        val uri = mockk<Uri>()
        val event = mockk<INotificationClickEvent>()
        val notification = mockk<INotification>()
        val clickListener = slot<INotificationClickListener>()
        every { Uri.parse("") } returns uri
        every { event.notification.additionalData } returns null
        every { event.notification } returns notification
        every { notification.additionalData } returns null
        every { notification.additionalData?.has("deeplink") } returns true
        every { notification.additionalData?.optString("deeplink") } returns ""
        every {
            OneSignal.Notifications.addClickListener(listener = capture(clickListener))
        } answers {
            clickListener.captured.onClick(event)
        }

        runTest {
            notificationsProvider.addClickListener()

            verify(exactly = 1) {
                OneSignal.Notifications.addClickListener(any())

                notificationsProvider.handleAdditionalData(notification.additionalData, null)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called with valid additional data, then a new activity is started with an intent`() {
        val uri = mockk<Uri>()
        val intent = spyk<Intent>()
        val additionalData = mockk<JSONObject>()

        every { uri.scheme } returns "scheme"
        every { uri.host } returns "host"
        every { Uri.parse("scheme://host") } returns uri
        every { intent.setData(uri) } returns intent
        every { intent.data } returns uri
        every { intent.setFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK) } returns intent
        every { intent.flags } returns (FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        every { additionalData.has("deeplink") } returns true
        every { additionalData.optString("deeplink") } returns "scheme://host"

        runTest {
            notificationsProvider.handleAdditionalData(additionalData, intent)

            assertEquals("scheme", intent.data?.scheme)
            assertEquals("host", intent.data?.host)
            assertEquals(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK, intent.flags)

            verify(exactly = 1) {
                context.startActivity(intent)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called without a deep link, then start activity is not called`() {
        val intent = spyk<Intent>()
        val additionalData = mockk<JSONObject>()

        every { additionalData.has("deeplink") } returns false

        runTest {
            notificationsProvider.handleAdditionalData(additionalData, intent)

            verify(exactly = 0) {
                context.startActivity(intent)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called and additional data is null, then start activity is not called`() {
        val intent = spyk<Intent>()

        val additionalData: JSONObject? = null

        runTest {
            notificationsProvider.handleAdditionalData(additionalData, intent)

            verify(exactly = 0) {
                context.startActivity(intent)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called and intent is null, then start activity is not called`() {
        val intent: Intent? = null
        val additionalData = mockk<JSONObject>()

        every { additionalData.optString("deeplink") } returns "deeplink"

        runTest {
            notificationsProvider.handleAdditionalData(additionalData, intent)

            verify(exactly = 0) {
                context.startActivity(intent)
            }
        }
    }
}
