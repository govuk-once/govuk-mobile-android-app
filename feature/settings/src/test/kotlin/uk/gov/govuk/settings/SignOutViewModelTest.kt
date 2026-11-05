package uk.gov.govuk.settings

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.auth.ErrorEvent.UnableToSignOutError
import uk.gov.govuk.settings.NavigationEvent.Error

@OptIn(ExperimentalCoroutinesApi::class)
class SignOutViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    private lateinit var viewModel: SignOutViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = SignOutViewModel(authRepo, analyticsClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SignOutScreen",
                screenName = "Sign Out",
                title = "Sign Out"
            )
        }
    }

    @Test
    fun `Given an error page view, then log analytics`() {
        viewModel.onErrorPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SignOutErrorScreen",
                screenName = "Sign Out",
                title = "Sign Out"
            )
        }
    }

    @Test
    fun `Given a back to settings button click, then log analytics`() {
        viewModel.onBack("button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Settings"
            )
        }
    }

    @Test
    fun `Given a user signs out successfully, then log analytics and emit nav event`() {
        coEvery { authRepo.clear() } returns true

        runTest {
            val navEvents = mutableListOf<NavigationEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.navigationEvent.toList(navEvents)
            }

            viewModel.onSignOut("text")

            assertEquals(1, navEvents.size)
            assertTrue(navEvents.first() is NavigationEvent.Success)
        }

        coVerify {
            analyticsClient.buttonClick(
                text = "text",
                section = "Settings"
            )

            authRepo.clear()
        }

        coVerify(exactly = 0) {
            analyticsClient.disable()
        }
    }

    @Test
    fun `Given a user signs out unsuccessfully, then log analytics and emit error`() {
        coEvery { authRepo.clear() } returns false

        runTest {
            val navEvents = mutableListOf<NavigationEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.navigationEvent.toList(navEvents)
            }

            viewModel.onSignOut("text")

            assertEquals(1, navEvents.size)
            assertEquals(Error(UnableToSignOutError), navEvents.first())
        }

        coVerify {
            analyticsClient.buttonClick(
                text = "text",
                section = "Settings"
            )

            authRepo.clear()
        }

        coVerify (exactly = 0) { analyticsClient.disable() }
    }
}
