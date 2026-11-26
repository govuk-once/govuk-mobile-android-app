package uk.gov.govuk.dvla.ui.component

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

@RunWith(AndroidJUnit4::class)
class LicenceRevokedForMedicalReasonsCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val title = getString(R.string.licence_revoked_medical_title)
    private val description = getString(R.string.licence_revoked_medical_description)
    private val linkText = getString(R.string.licence_revoked_medical_link_text)

    @Test
    fun revoked_title_is_displayed() {
        composeTestRule.setContent {
            GovUkTheme {
                LicenceRevokedForMedicalReasonsCard(
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    @Test
    fun revoked_description_is_displayed() {
        composeTestRule.setContent {
            GovUkTheme {
                LicenceRevokedForMedicalReasonsCard(
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(description).assertIsDisplayed()
    }

    @Test
    fun reapply_link_is_displayed() {
        composeTestRule.setContent {
            GovUkTheme {
                LicenceRevokedForMedicalReasonsCard(
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(linkText).assertIsDisplayed()
    }

    @Test
    fun clicking_reapply_link_invokes_callback_with_link_text() {
        var clickedText: String? = null

        composeTestRule.setContent {
            GovUkTheme {
                LicenceRevokedForMedicalReasonsCard(
                    onClick = { text ->
                        clickedText =
                            text
                    }
                )
            }
        }

        composeTestRule.onNodeWithText(linkText).performClick()

        assertEquals(linkText, clickedText)
    }

    private fun getString(
        @StringRes id: Int
    ): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
}
