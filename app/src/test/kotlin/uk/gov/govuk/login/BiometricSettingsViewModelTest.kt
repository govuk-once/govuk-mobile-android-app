package uk.gov.govuk.login

import android.os.Build
import androidx.fragment.app.FragmentActivity
import io.mockk.coVerify
import io.mockk.every
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
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.R
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricSettingsViewModelTest {

    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = BiometricSettingsViewModel(appRepo, authRepo, analyticsClient)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "BiometricSettingsScreen",
                screenName = "Biometric Settings",
                title = "Biometric Settings"
            )
        }
    }

    @Test
    fun `Given a user is signed in, when toggle, then clear auth repo, log analytics and emit ui state`() {
        every { authRepo.isUserSignedIn() } returns true andThen true andThen false

        val viewModel = BiometricSettingsViewModel(appRepo, authRepo, analyticsClient)

        runTest {
            val uiStates = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(uiStates)
            }

            viewModel.onToggle("text", activity)

            verify {
                analyticsClient.toggleFunction(
                    "text",
                    BIOMETRICS_SECTION,
                    BIOMETRICS_SETTINGS_DISABLE
                )
            }
            coVerify { authRepo.clear() }

            assertFalse(uiStates.last())
        }
    }

    @Test
    fun `Given a user is not signed in, when toggle, then persist refresh token, log analytics and emit ui state`() {
        every { authRepo.isUserSignedIn() } returns false andThen false andThen true

        val viewModel = BiometricSettingsViewModel(appRepo, authRepo, analyticsClient)

        runTest {
            val uiStates = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(uiStates)
            }

            viewModel.onToggle("text", activity)

            verify {
                analyticsClient.toggleFunction(
                    "text",
                    BIOMETRICS_SECTION,
                    BIOMETRICS_SETTINGS_ENABLE
                )
            }
            coVerify { authRepo.persistRefreshToken(any(), any()) }

            assertTrue(uiStates.last())
        }
    }

    @Test
    fun `Given the app is running on an Android 10 device, when get description two is called, then return the correct resource`() {
        val viewModel = BiometricSettingsViewModel(appRepo, authRepo, analyticsClient)
        val descriptionTwo = viewModel.getDescriptionTwo(Build.VERSION_CODES.Q)

        assertEquals(R.string.biometric_settings_android_10_description_2, descriptionTwo)
    }

    @Test
    fun `Given the app is running on an Android 11 device, when get description two is called, then return the correct resource`() {
        val viewModel = BiometricSettingsViewModel(appRepo, authRepo, analyticsClient)
        val descriptionTwo = viewModel.getDescriptionTwo(Build.VERSION_CODES.R)

        assertEquals(R.string.biometric_settings_android_11_description_2, descriptionTwo)
    }
}
