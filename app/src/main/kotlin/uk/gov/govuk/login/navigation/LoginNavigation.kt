package uk.gov.govuk.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.data.auth.ErrorEvent
import uk.gov.govuk.login.ui.AppUnavailableRoute
import uk.gov.govuk.login.LoginEvent
import uk.gov.govuk.login.ui.BiometricRoute
import uk.gov.govuk.login.ui.BiometricSettingsRoute
import uk.gov.govuk.login.ui.ErrorRoute
import uk.gov.govuk.login.ui.LoginRoute

const val LOGIN_GRAPH_ROUTE = "login_graph_route"
const val LOGIN_ROUTE = "login_route"
private const val BIOMETRIC_ROUTE = "biometric_route"
const val BIOMETRIC_SETTINGS_ROUTE = "biometric_settings_route"
private const val ERROR_ROUTE = "login_error_route"
private const val USER_API_ERROR_ROUTE = "user_api_error_route"

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onLoginCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = LOGIN_GRAPH_ROUTE,
        startDestination = LOGIN_ROUTE
    ) {
        composable(route = LOGIN_ROUTE) {
            LoginRoute(
                onLoginCompleted = { loginEvent ->
                    if (loginEvent is LoginEvent.WebLogin
                        && loginEvent.isBiometricsEnabled) {
                        navController.popBackStack()
                        navController.navigate(BIOMETRIC_ROUTE)
                    } else {
                        onLoginCompleted()
                    }
                },
                onError = { event ->
                    when(event) {
                        is ErrorEvent.UnableToSignInError, ErrorEvent.UnableToSignOutError ->
                            navController.navigate(ERROR_ROUTE)
                        is ErrorEvent.UserApiError ->
                            navController.navigate(USER_API_ERROR_ROUTE)
                    }
                },
                modifier = modifier
            )
        }
        composable(BIOMETRIC_ROUTE) {
            BiometricRoute(
                onCompleted = { onLoginCompleted() },
                modifier = modifier
            )
        }
        composable(BIOMETRIC_SETTINGS_ROUTE) {
            BiometricSettingsRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
        composable(route = ERROR_ROUTE) {
            ErrorRoute(
                onBack = {
                    navController.popBackStack(ERROR_ROUTE, true)
                },
                modifier = modifier
            )
        }

        composable(route = USER_API_ERROR_ROUTE) {
            AppUnavailableRoute()
        }
    }
}
