package uk.gov.govuk.dvla.ui

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UrlModel

@RunWith(AndroidJUnit4::class)
class LicenceViewContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val revokedTitle = getString(R.string.licence_revoked_medical_title)
    private val revokedLinkText = getString(R.string.licence_revoked_medical_link_text)

    @Test
    fun given_revoked_medical_licence_state_revoked_card_is_displayed() {
        composeTestRule.setContent {
            GovUkTheme {
                LicenceViewContent(
                    launchBrowser = { _, _ -> },
                    licenceState = LicenceSummaryUiState.LicenceRevokedForMedicalReasons(
                        url = UrlModel(originalUrl = "https://example.com")
                    ),
                    onMenuItemClick = {},
                    onLicenceNumberLongClick = {},
                    onCategoriesClick = {},
                    onPageView = {}
                )
            }
        }

        composeTestRule.onNodeWithText(revokedTitle).assertIsDisplayed()
    }

    @Test
    fun given_revoked_medical_state_when_link_clicked_launchBrowser_called_with_url() {
        val revokedUrl = "https://example.com/revoked"
        var launchedUrl: String? = null

        composeTestRule.setContent {
            GovUkTheme {
                LicenceViewContent(
                    launchBrowser = { _, url -> launchedUrl = url.urlToOpen },
                    licenceState = LicenceSummaryUiState.LicenceRevokedForMedicalReasons(
                        url = UrlModel(originalUrl = revokedUrl)
                    ),
                    onMenuItemClick = {},
                    onLicenceNumberLongClick = {},
                    onCategoriesClick = {},
                    onPageView = {}
                )
            }
        }

        composeTestRule.onNodeWithText(revokedLinkText).performClick()

        assertEquals(revokedUrl, launchedUrl)
    }

    private fun getString(
        @StringRes id: Int
    ): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
}
