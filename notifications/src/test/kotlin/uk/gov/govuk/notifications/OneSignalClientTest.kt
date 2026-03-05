package uk.gov.govuk.notifications

import android.content.Context
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.notifications.navigation.DeepLinkLauncher

@OptIn(ExperimentalCoroutinesApi::class)
class OneSignalClientTest {
    private val context = mockk<Context>(relaxed = true)
    private val deepLinkLauncher = mockk<DeepLinkLauncher>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var notificationsProvider: NotificationsProvider

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        notificationsProvider = OneSignalClient(context, deepLinkLauncher)

        mockkStatic(OneSignal::class)
        mockkStatic(OneSignal.Debug::class)
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
        val additionalData = mockk<JSONObject>()
        val event = mockk<INotificationClickEvent>()
        val notification = mockk<INotification>()
        val clickListener = slot<INotificationClickListener>()

        every { event.notification } returns notification
        every { notification.additionalData } returns additionalData
        every { notification.additionalData?.optString("deeplink") } returns "https://gov.uk"
        every {
            OneSignal.Notifications.addClickListener(listener = capture(clickListener))
        } answers {
            clickListener.captured.onClick(event)
        }

        runTest {
            notificationsProvider.addClickListener()

            verify(exactly = 1) {
                OneSignal.Notifications.addClickListener(any())
                deepLinkLauncher.launchDeepLink("https://gov.uk")            }
        }
    }



    @Test
    fun `Given a notification click without a deep link, then the launcher is not called`() {
        val additionalData = mockk<JSONObject>()
        val event = mockk<INotificationClickEvent>()
        val notification = mockk<INotification>()
        val clickListenerSlot = slot<INotificationClickListener>()

        every { event.notification } returns notification
        every { notification.additionalData } returns additionalData

        every { additionalData.optString("deeplink") } returns ""

        every {
            OneSignal.Notifications.addClickListener(capture(clickListenerSlot))
        } returns Unit

        runTest {
            notificationsProvider.addClickListener()
            clickListenerSlot.captured.onClick(event)

            verify(exactly = 0) {
                deepLinkLauncher.launchDeepLink(any())
            }
        }
    }

    @Test
    fun `Given a notification click where additional data is null, then the launcher is not called`() {
        val event = mockk<INotificationClickEvent>()
        val notification = mockk<INotification>()
        val clickListenerSlot = slot<INotificationClickListener>()

        every { event.notification } returns notification

        every { notification.additionalData } returns null

        every {
            OneSignal.Notifications.addClickListener(capture(clickListenerSlot))
        } returns Unit

        runTest {
            notificationsProvider.addClickListener()
            clickListenerSlot.captured.onClick(event)

            verify(exactly = 0) {
                deepLinkLauncher.launchDeepLink(any())
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when login is called, then One Signal login is called`() {
        every { OneSignal.login("12345") } returns Unit

        runTest {
            notificationsProvider.login("12345")

            verify(exactly = 1) {
                OneSignal.login("12345")
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when logout is called, then One Signal logout is called`() {
        every { OneSignal.logout() } returns Unit

        runTest {
            notificationsProvider.logout()

            verify(exactly = 1) {
                OneSignal.logout()
            }
        }
    }
}
