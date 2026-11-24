package uk.gov.govuk.dwp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dwp.ui.DwpLinkScreen

@RunWith(AndroidJUnit4::class)
class DwpLinkFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<String>()

    private fun showScreen(
        state: DwpLinkViewModel.UiState
    ) {
        composeTestRule.setContent {
            GovUkTheme {
                DwpLinkScreen(
                    state = state,
                    onClose = { actions.add("close") },
                    onContinue = { actions.add("continue") }
                )
            }
        }
    }

    @Test
    fun introductionContinueCallsOnContinue() {
        showScreen(DwpLinkViewModel.UiState.Default)
        composeTestRule.onNodeWithText("Add your benefits and state pension payment information").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").performClick()
        assertEquals(listOf("continue"), actions)
    }

    @Test
    fun introductionCloseCallsOnClose() {
        showScreen(DwpLinkViewModel.UiState.Default)
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(uk.gov.govuk.design.R.string.content_desc_close)
        ).performClick()
        assertEquals(listOf("close"), actions)
    }

    @Test
    fun androidBackCallsOnClose() {
        showScreen(DwpLinkViewModel.UiState.Default)
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()
        assertEquals(listOf("close"), actions)
    }

    @Test
    fun successContinueCallsOnClose() {
        showScreen(DwpLinkViewModel.UiState.Success)
        composeTestRule.onNodeWithText("Benefits payment information linked").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").performClick()
        assertEquals(listOf("close"), actions)
    }

    @Test
    fun authenticationLoadingShowsProgressWithoutContinue() {
        assertLoading(DwpLinkViewModel.UiState.Loading.Auth)
    }

    @Test
    fun linkingLoadingShowsProgressWithoutContinue() {
        assertLoading(DwpLinkViewModel.UiState.Loading.Linking)
    }

    private fun assertLoading(
        state: DwpLinkViewModel.UiState.Loading
    ) {
        showScreen(state)
        composeTestRule.onNodeWithText("Linking payment information").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").assertDoesNotExist()
    }

    @Test
    fun offlineErrorContinueCallsOnContinue() {
        assertRetry(DwpLinkViewModel.UiState.Error.Offline)
    }

    @Test
    fun otherErrorContinueCallsOnContinue() {
        assertRetry(DwpLinkViewModel.UiState.Error.Other)
    }

    private fun assertRetry(
        state: DwpLinkViewModel.UiState.Error
    ) {
        showScreen(state)
        composeTestRule.onNodeWithText("We could not link your payment information. Please try again.")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").performClick()
        assertEquals(listOf("continue"), actions)
    }
}
