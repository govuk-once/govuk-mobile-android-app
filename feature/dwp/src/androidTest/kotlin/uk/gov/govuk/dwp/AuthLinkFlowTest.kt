package uk.gov.govuk.dwp

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.design.ui.component.NavigationCard
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dwp.data.AuthRepo
import uk.gov.govuk.dwp.di.AuthModule.TEMPORARY_LINKING_TOKEN
import uk.gov.govuk.dwp.linking.data.LinkingRepo
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse
import uk.gov.govuk.dwp.navigation.DWP_GRAPH_ROUTE
import uk.gov.govuk.dwp.navigation.dwpGraph
import uk.gov.govuk.dwp.navigation.navigateToDwpLinkIntro
import uk.gov.govuk.dwp.ui.DwpLinkHeaderContent
import uk.gov.govuk.dwp.ui.DwpLinkScreen

class AuthLinkFlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private val dwpAuthUrl = "https://dwp-auth/test-sign-in"
    private val verificationToken = "verification"
    private lateinit var linkViewModel: AuthViewModel
    private val repository = mockk<AuthRepo>()
    private val linkingRepo = mockk<LinkingRepo>()
    private val linkState = MutableStateFlow(ServiceLinkStatus.UNLINKED)
    private var submissions = 0

    @Before
    fun setup() {
        every { repository.linkState } returns linkState
        coEvery { linkingRepo.getVerification() } returns Result.Success(VerificationResponse(verificationToken))
        coEvery { repository.linkAccount(any()) } coAnswers {
            assertEquals(TEMPORARY_LINKING_TOKEN, firstArg<String>())
            submissions++
            linkState.value = ServiceLinkStatus.LINKED
            Result.Success(Unit)
        }
    }
    private lateinit var navController: NavHostController

    private fun launch() {
        compose.setContent {
            GovUkTheme {
                navController = rememberNavController()
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        NavigationCard(title = "Benefits", onClick = { navController.navigate("benefits") })
                    }
                    composable("benefits") {
                        val widgetViewModel = androidx.lifecycle.viewmodel.compose.viewModel<AuthLinkWidgetViewModel>(
                            factory = viewModelFactory {
                                initializer { AuthLinkWidgetViewModel(repository) }
                            }
                        )
                        val state by widgetViewModel.dwpState.collectAsState(initial = ServiceLinkStatus.CHECKING)
                        DwpLinkHeaderContent(state, onActionClick = {
                            navController.navigateToDwpLinkIntro()
                        })
                    }
                    dwpGraph(onClose = {
                        navController.popBackStack(DWP_GRAPH_ROUTE, inclusive = true)
                    }, linkContent = { onClose ->
                        val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<AuthViewModel>(
                            factory = viewModelFactory {
                                initializer {
                                    AuthViewModel(repository, linkingRepo, dwpAuthUrl).also { linkViewModel = it }
                                }
                            }
                        )
                        val state by viewModel.uiState.collectAsState()
                        DwpLinkScreen(
                            state,
                            onClose,
                            viewModel::onIntroContinueClicked
                        )
                    })
                }
            }
        }
        compose.onNodeWithContentDescription("Benefits", substring = true).performClick()
        compose.onNodeWithText("Add payment information").performClick()
        compose.onNodeWithText("Add your benefits and state pension payment information").assertIsDisplayed()
    }

    private fun back() {
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    @Test
    fun closeReturnsToBenefitsWithoutLinking() {
        launch()
        compose.onNodeWithContentDescription(compose.activity.getString(uk.gov.govuk.design.R.string.content_desc_close)).performClick()
        compose.onNodeWithText("Add payment information").assertIsDisplayed()
        assertEquals(0, submissions)
    }

    @Test
    fun androidBackReturnsToBenefitsWithoutLinking() {
        launch()
        back()
        compose.onNodeWithText("Add payment information").assertIsDisplayed()
        assertEquals(0, submissions)
    }

    @Test
    fun successfulLinkReturnsToBenefitsAndBackDoesNotReopenFlow() {
        launch()
        compose.onNodeWithText("Continue").performClick()
        compose.onNodeWithText("Benefits payment information linked").assertIsDisplayed()
        compose.runOnIdle {
            val url = Uri.parse(linkViewModel.authUrlToLaunch.value)
            assertEquals("dwp-auth", url.host)
            assertEquals("/test-sign-in", url.path)
            assertEquals(verificationToken, url.getQueryParameter("token"))
        }
        compose.onNodeWithText("Continue").performClick()
        compose.runOnIdle { assertEquals("benefits", navController.currentDestination?.route) }
        assertEquals(1, submissions)
        compose.onNodeWithText("Add payment information").assertDoesNotExist()
        back()
        compose.runOnIdle { assertEquals("home", navController.currentDestination?.route) }
        compose.onNodeWithContentDescription("Benefits", substring = true).assertIsDisplayed()
    }
}
