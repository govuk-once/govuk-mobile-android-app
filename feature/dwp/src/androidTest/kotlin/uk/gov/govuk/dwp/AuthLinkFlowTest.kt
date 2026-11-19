package uk.gov.govuk.dwp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.design.ui.component.NavigationCard
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dwp.navigation.DWP_GRAPH_ROUTE
import uk.gov.govuk.dwp.navigation.dwpGraph
import uk.gov.govuk.dwp.navigation.navigateToDwpLinkIntro
import uk.gov.govuk.dwp.ui.DwpLinkHeaderContent
import uk.gov.govuk.dwp.ui.DwpLinkScreen

@RunWith(AndroidJUnit4::class)
class AuthLinkFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private var linkState by mutableStateOf(ServiceLinkStatus.UNLINKED)
    private var screenState by mutableStateOf<AuthViewModel.UiState>(AuthViewModel.UiState.Default)
    private var submissions = 0

    private lateinit var navController: NavHostController

    private fun launch() {
        composeTestRule.setContent {
            GovUkTheme {
                navController = rememberNavController()
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        NavigationCard(title = "Benefits", onClick = { navController.navigate("benefits") })
                    }
                    composable("benefits") {
                        DwpLinkHeaderContent(linkState, onActionClick = {
                            navController.navigateToDwpLinkIntro()
                        })
                    }
                    dwpGraph(onClose = {
                        navController.popBackStack(DWP_GRAPH_ROUTE, inclusive = true)
                    }, linkContent = { onClose ->
                        DwpLinkScreen(
                            state = screenState,
                            onClose = onClose,
                            onContinue = {
                                submissions++
                                linkState = ServiceLinkStatus.LINKED
                                screenState = AuthViewModel.UiState.Success
                            }
                        )
                    })
                }
            }
        }
        composeTestRule.onNodeWithContentDescription("Benefits", substring = true).performClick()
        composeTestRule.onNodeWithText("Add payment information").performClick()
        composeTestRule.onNodeWithText("Add your benefits and state pension payment information").assertIsDisplayed()
    }

    private fun back() {
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()
    }

    @Test
    fun closeReturnsToBenefitsWithoutLinking() {
        launch()
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(uk.gov.govuk.design.R.string.content_desc_close)
        ).performClick()
        composeTestRule.onNodeWithText("Add payment information").assertIsDisplayed()
        assertEquals(0, submissions)
    }

    @Test
    fun androidBackReturnsToBenefitsWithoutLinking() {
        launch()
        back()
        composeTestRule.onNodeWithText("Add payment information").assertIsDisplayed()
        assertEquals(0, submissions)
    }

    @Test
    fun successfulLinkReturnsToBenefitsAndBackDoesNotReopenFlow() {
        launch()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.onNodeWithText("Benefits payment information linked").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.runOnIdle { assertEquals("benefits", navController.currentDestination?.route) }
        assertEquals(1, submissions)
        composeTestRule.onNodeWithText("Add payment information").assertDoesNotExist()
        back()
        composeTestRule.runOnIdle { assertEquals("home", navController.currentDestination?.route) }
        composeTestRule.onNodeWithContentDescription("Benefits", substring = true).assertIsDisplayed()
    }
}
