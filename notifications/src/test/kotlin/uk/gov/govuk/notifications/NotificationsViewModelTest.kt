package uk.gov.govuk.notifications

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val notificationsProvider = mockk<NotificationsProvider>()
    private val notificationsDataStore = mockk<NotificationsDataStore>()

    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsViewModel(analyticsClient, notificationsProvider, notificationsDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        runTest {
            verify(exactly = 1) {
                analyticsClient.screenView(
                    screenClass = "NotificationsOnboardingScreen",
                    screenName = "NotificationsOnboardingScreen",
                    title = "NotificationsOnboardingScreen"
                )
            }
        }
    }

    @Test
    fun `Given Allow notifications button click, then give consent and log analytics`() {
        every { notificationsProvider.giveConsent() } returns Unit

        viewModel.onGiveConsentClick("Title") {}

        runTest {
            verify(exactly = 1) {
                notificationsProvider.giveConsent()
                analyticsClient.buttonClick("Title")
            }
        }
    }

    @Test
    fun `Given Turn off notifications button click, then log analytics`() {
        viewModel.onTurnOffNotificationsClick("Title")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick(
                    text = "Title",
                    external = true
                )
            }
        }
    }

    @Test
    fun `Given Allow notifications button click, then first permission request completed, request permission and log analytics`() {
        coEvery { notificationsDataStore.firstPermissionRequestCompleted() } returns Unit
        coEvery { notificationsProvider.requestPermission() } returns Unit
        every { notificationsProvider.giveConsent() } returns Unit

        viewModel.onAllowNotificationsClick("Title") {}

        runTest {
            coVerify(exactly = 1) {
                notificationsDataStore.firstPermissionRequestCompleted()
            }
            coVerify(exactly = 1) {
                notificationsProvider.giveConsent()
                notificationsProvider.requestPermission()

                analyticsClient.buttonClick("Title")
            }
        }
    }

    @Test
    fun `Given Not now button click, then log analytics`() {

        viewModel.onNotNowClick("Title")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick("Title")
            }
        }
    }

    @Test
    fun `Given Privacy policy link click, then log analytics`() {
        viewModel.onPrivacyPolicyClick("Text", "Url")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick(
                    text = "Text",
                    url = "Url",
                    external = true
                )
            }
        }
    }

    @Test
    fun `Given Continue button click, then remove consent and log analytics`() {
        every { notificationsProvider.removeConsent() } returns Unit

        viewModel.onContinueButtonClick("Text")

        runTest {
            verify(exactly = 1) {
                notificationsProvider.removeConsent()
                analyticsClient.buttonClick(
                    text = "Text"
                )
            }
        }
    }

    @Test
    fun `Given Cancel button click, then log analytics`() {
        viewModel.onCancelButtonClick("Text")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick(
                    text = "Text"
                )
            }
        }
    }
}
