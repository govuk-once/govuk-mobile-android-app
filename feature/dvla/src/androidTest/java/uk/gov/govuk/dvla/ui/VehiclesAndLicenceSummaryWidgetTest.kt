package uk.gov.govuk.dvla.ui

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehiclesAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState

@RunWith(AndroidJUnit4::class)
class VehiclesAndLicenceSummaryWidgetTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val title = getString(R.string.licence_revoked_medical_title)
    private val linkText = getString(R.string.licence_revoked_medical_link_text)
    private val revokedUrl = UrlModel("https://www.gov.uk/reapply-driving-licence-health-condition")

    private fun fakeViewModel(
        state: UiState
    ): VehiclesAndLicenceSummaryViewModel = mockk(relaxed = true) {
        every { uiState } returns MutableStateFlow(state)
        every { dvlaUrls } returns null
    }

    @Test
    fun given_revoked_medical_licence_state_then_revoked_card_is_displayed() {
        composeTestRule.setContent {
            GovUkTheme {
                VehiclesAndLicenceSummaryWidget(
                    launchBrowser = {},
                    onVehicleDetailsClick = {},
                    onDrivingCategoriesClick = {},
                    viewModel = fakeViewModel(
                        UiState.Default(
                            drivingView = DrivingView.LICENCE,
                            licenceState = LicenceSummaryUiState.RevokedMedicalLicence(revokedUrl)
                        )
                    )
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(linkText).assertIsDisplayed()
    }

    @Test
    fun given_revoked_medical_state_when_reapply_link_clicked_then_launchBrowser_called_with_correct_url() {
        var launchedUrl: String? = null

        composeTestRule.setContent {
            GovUkTheme {
                VehiclesAndLicenceSummaryWidget(
                    launchBrowser = { url -> launchedUrl = url },
                    onVehicleDetailsClick = {},
                    onDrivingCategoriesClick = {},
                    viewModel = fakeViewModel(
                        UiState.Default(
                            drivingView = DrivingView.LICENCE,
                            licenceState = LicenceSummaryUiState.RevokedMedicalLicence(revokedUrl)
                        )
                    )
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(linkText).performClick()

        assertEquals(revokedUrl.urlToOpen, launchedUrl)
    }

    @Test
    fun given_revoked_medical_state_when_reapply_link_clicked_then_analytics_event_is_fired() {
        val viewModel = fakeViewModel(
            UiState.Default(
                drivingView = DrivingView.LICENCE,
                licenceState = LicenceSummaryUiState.RevokedMedicalLicence(revokedUrl)
            )
        )

        composeTestRule.setContent {
            GovUkTheme {
                VehiclesAndLicenceSummaryWidget(
                    launchBrowser = {},
                    onVehicleDetailsClick = {},
                    onDrivingCategoriesClick = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(linkText).performClick()

        verify(exactly = 1) {
            viewModel.onExternalButtonClicked(
                text = linkText,
                url = revokedUrl.originalUrl
            )
        }
    }

    @Test
    fun given_vehicles_tab_selected_then_revoked_card_is_not_shown() {
        composeTestRule.setContent {
            GovUkTheme {
                VehiclesAndLicenceSummaryWidget(
                    launchBrowser = {},
                    onVehicleDetailsClick = {},
                    onDrivingCategoriesClick = {},
                    viewModel = fakeViewModel(
                        UiState.Default(
                            drivingView = DrivingView.VEHICLES,
                            vehiclesState = VehiclesSummaryUiState.Loading
                        )
                    )
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(title).assertDoesNotExist()
    }

    @Test
    fun given_hidden_state_then_revoked_card_is_not_shown() {
        composeTestRule.setContent {
            GovUkTheme {
                VehiclesAndLicenceSummaryWidget(
                    launchBrowser = {},
                    onVehicleDetailsClick = {},
                    onDrivingCategoriesClick = {},
                    viewModel = fakeViewModel(UiState.Hidden)
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(title).assertDoesNotExist()
    }

    private fun getString(
        @StringRes id: Int
    ): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
}
