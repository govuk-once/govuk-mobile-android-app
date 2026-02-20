package uk.gov.govuk.settings

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.settings.BuildConfig.ACCESSIBILITY_STATEMENT_EVENT
import uk.gov.govuk.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.gov.govuk.settings.BuildConfig.ACCOUNT_EVENT
import uk.gov.govuk.settings.BuildConfig.ACCOUNT_URL
import uk.gov.govuk.settings.BuildConfig.HELP_AND_FEEDBACK_EVENT
import uk.gov.govuk.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.gov.govuk.settings.BuildConfig.NOTIFICATIONS_PERMISSION_EVENT
import uk.gov.govuk.settings.BuildConfig.OPEN_SOURCE_LICENCE_EVENT
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_EVENT
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.settings.BuildConfig.SIGN_OUT_EVENT
import uk.gov.govuk.settings.BuildConfig.TERMS_AND_CONDITIONS_EVENT
import uk.gov.govuk.settings.BuildConfig.TERMS_AND_CONDITIONS_URL

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        every { authRepo.getUserEmail() } returns "user@email.com"
        coEvery { authRepo.isAuthenticationEnabled() } returns true
        coEvery { analyticsClient.isAnalyticsEnabled() } returns true
        coEvery { flagRepo.isNotificationsEnabled() } returns true

        viewModel = SettingsViewModel(authRepo, flagRepo, analyticsClient, configRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given analytics are enabled, When init, then return analytics enabled`() {
        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isAnalyticsEnabled)
        }
    }

    @Test
    fun `Given analytics are disabled, When init, then return analytics disabled`() {
        coEvery { analyticsClient.isAnalyticsEnabled() } returns false

        val viewModel = SettingsViewModel(authRepo, flagRepo, analyticsClient, configRepo)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isAnalyticsEnabled)
        }
    }

    @Test
    fun `Given notifications are enabled, When init, then return notifications enabled`() {
        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isNotificationsEnabled)
        }
    }

    @Test
    fun `Given notifications are disabled, When init, then return notifications disabled`() {
        coEvery { flagRepo.isNotificationsEnabled() } returns false

        val viewModel = SettingsViewModel(authRepo, flagRepo, analyticsClient, configRepo)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isNotificationsEnabled)
        }
    }

    @Test
    fun `Given authentication is enabled, When init, then return authentication enabled`() {
        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isAuthenticationEnabled)
        }
    }

    @Test
    fun `Given authentication is disabled, When init, then return authentication disabled`() {
        every { authRepo.isAuthenticationEnabled() } returns false

        val viewModel = SettingsViewModel(authRepo, flagRepo, analyticsClient, configRepo)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isAuthenticationEnabled)
        }
    }

    @Test
    fun `Given a user is signed in, When init, then return user email`() {
        runTest {
            val result = viewModel.uiState.first()
            assertEquals("user@email.com", result!!.userEmail)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SettingsScreen",
                screenName = "Settings",
                title = "Settings"
            )
        }
    }

    @Test
    fun `Given analytics have been enabled, then update and emit ui state`() {
        viewModel.onAnalyticsConsentChanged(true)

        runTest {
            val result = viewModel.uiState.value
            assertTrue(result!!.isAnalyticsEnabled)

            coVerify {
                analyticsClient.enable()
            }
        }
    }

    @Test
    fun `Given analytics have been disabled, then update, clear remote config and emit ui state`() {
        viewModel.onAnalyticsConsentChanged(false)

        runTest {
            val result = viewModel.uiState.value
            assertFalse(result!!.isAnalyticsEnabled)

            coVerify {
                analyticsClient.disable()
                configRepo.clearRemoteConfigValues()
            }
        }
    }

    @Test
    fun `Given a license page view, then log analytics`() {
        viewModel.onLicenseView()

        verify {
            analyticsClient.screenView(
                screenClass = OPEN_SOURCE_LICENCE_EVENT,
                screenName = OPEN_SOURCE_LICENCE_EVENT,
                title = OPEN_SOURCE_LICENCE_EVENT
            )
        }
    }

    @Test
    fun `Given a help and feedback page view, then log analytics`() {
        viewModel.onHelpAndFeedbackView()

        verify {
            analyticsClient.settingsItemClick(
                text = HELP_AND_FEEDBACK_EVENT,
                url = HELP_AND_FEEDBACK_URL
            )
        }
    }

    @Test
    fun `Given a privacy policy page view, then log analytics`() {
        viewModel.onPrivacyPolicyView()

        verify {
            analyticsClient.settingsItemClick(
                text = PRIVACY_POLICY_EVENT,
                url = PRIVACY_POLICY_URL
            )
        }
    }

    @Test
    fun `Given a accessibility statement page view, then log analytics`() {
        viewModel.onAccessibilityStatementView()

        analyticsClient.settingsItemClick(
            text = ACCESSIBILITY_STATEMENT_EVENT,
            url = ACCESSIBILITY_STATEMENT_URL
        )
    }

    @Test
    fun `Given a terms and conditions page view, then log analytics`() {
        viewModel.onTermsAndConditionsView()

        analyticsClient.settingsItemClick(
            text = TERMS_AND_CONDITIONS_EVENT,
            url = TERMS_AND_CONDITIONS_URL
        )
    }

    @Test
    fun `Given a notifications click, then log analytics`() {
        viewModel.onNotificationsClick()

        analyticsClient.screenView(
            screenClass = NOTIFICATIONS_PERMISSION_EVENT,
            screenName = NOTIFICATIONS_PERMISSION_EVENT,
            title = NOTIFICATIONS_PERMISSION_EVENT
        )
    }

    @Test
    fun `Given a biometrics click, then log analytics`() {
        viewModel.onBiometricsClick("text")

        verify {
            analyticsClient.settingsItemClick(
                text = "text",
                external = false
            )
        }
    }

    @Test
    fun `Given a button click, then log analytics`() {
        viewModel.onButtonClick("Text")
        verify {
            analyticsClient.buttonClick("Text")
        }
    }

    @Test
    fun `Given an account view, then log analytics`() {
        viewModel.onAccount()
        verify {
            analyticsClient.settingsItemClick(
                ACCOUNT_EVENT,
                ACCOUNT_URL
            )
        }
    }

    @Test
    fun `Given a sign out, then log analytics`() {
        viewModel.onSignOut()
        verify {
            analyticsClient.settingsItemClick(
                text = SIGN_OUT_EVENT,
                external = false
            )
        }
    }
}
