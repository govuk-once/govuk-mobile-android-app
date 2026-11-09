package uk.gov.govuk.dvla.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import uk.gov.govuk.dvla.DvlaViewModel
import uk.gov.govuk.dvla.domain.EntitlementType
import uk.gov.govuk.dvla.ui.CheckVehicleDetailsRoute
import uk.gov.govuk.dvla.ui.DvlaLinkIntroScreen
import uk.gov.govuk.dvla.ui.DvlaLinkingRoute
import uk.gov.govuk.dvla.ui.LicenceCategoriesRoute
import uk.gov.govuk.dvla.ui.LicenceCategoryDetailRoute
import uk.gov.govuk.dvla.ui.VehicleDetailsRoute

const val DVLA_GRAPH_ROUTE = "dvla_graph_route"
const val DVLA_LINK_INTRO_ROUTE = "dvla_link_intro_route"
const val DVLA_LINK_ROUTE = "dvla_link_route"
const val DVLA_DEEP_LINK_PATH = "/callback/dvla/auth"
const val VEHICLE_DETAILS_ROUTE = "vehicle_details_route"
const val VEHICLE_SEARCH_RESULT_ROUTE = "vehicle_search_result_route"
const val LICENCE_CATEGORIES_ROUTE = "licence_categories_route"
const val LICENCE_CATEGORY_DETAIL_ROUTE = "licence_category_detail_route"
const val ARG_DVLA_TOKEN = "token"
const val ARG_VEHICLE_ID = "vehicle_id"
const val ARG_REG_NUMBER = "registration_number"
const val ARG_CATEGORY_CODE = "category_code"
const val ARG_ENTITLEMENT_TYPE = "entitlement_type"

fun NavGraphBuilder.dvlaGraph(
    onBack: () -> Unit,
    onIntroClose: () -> Unit,
    onContinueToLink: () -> Unit,
    launchBrowser: (String) -> Unit,
    onWebFlowClosed: () -> Unit,
    onLinkComplete: () -> Unit,
    onUnlinkComplete: () -> Unit,
    onVehicleFound: (String) -> Unit,
    onCategoryInfoClick: (code: String, type: EntitlementType) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = DVLA_GRAPH_ROUTE,
        startDestination = DVLA_LINK_INTRO_ROUTE
    ) {
        composable(route = DVLA_LINK_INTRO_ROUTE) {
            val viewModel: DvlaViewModel = hiltViewModel()

            DvlaLinkIntroScreen(
                onPageView = { screenTitle ->
                    viewModel.onIntroPageView(screenTitle)
                },
                onClose = {
                    viewModel.onIntroCloseClicked()
                    onIntroClose()
                },
                onContinue = { buttonText ->
                    viewModel.onIntroContinueClicked(buttonText)
                    onIntroClose()
                    onContinueToLink()
                },
                modifier = modifier
            )
        }

        composable(
            route = "$DVLA_LINK_ROUTE?$ARG_DVLA_TOKEN={$ARG_DVLA_TOKEN}",
            arguments = listOf(
                navArgument(ARG_DVLA_TOKEN) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            DvlaLinkingRoute(
                onLaunchBrowser = launchBrowser,
                onLinkComplete = onLinkComplete,
                onUnlinkComplete = onUnlinkComplete,
                onWebFlowClosed = onWebFlowClosed,
                modifier = modifier
            )
        }

        composable(
            route = "$VEHICLE_DETAILS_ROUTE/{$ARG_VEHICLE_ID}",
            arguments = listOf(
                navArgument(ARG_VEHICLE_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            VehicleDetailsRoute(
                launchBrowser = launchBrowser,
                onBack = onBack,
                modifier = modifier
            )
        }

        composable(
            route = "$VEHICLE_SEARCH_RESULT_ROUTE/{$ARG_REG_NUMBER}",
            arguments = listOf(
                navArgument(ARG_REG_NUMBER) { type = NavType.StringType }
            )
        ) {
            CheckVehicleDetailsRoute(
                launchBrowser = launchBrowser,
                onBack = onBack,
                modifier = modifier,
                onVehicleFound = onVehicleFound
            )
        }

        composable(route = LICENCE_CATEGORIES_ROUTE) {
            LicenceCategoriesRoute(
                launchBrowser = launchBrowser,
                onBack = onBack,
                onCategoryInfoClick = onCategoryInfoClick,
                modifier = modifier
            )
        }

        composable(
            route = "$LICENCE_CATEGORY_DETAIL_ROUTE/{$ARG_CATEGORY_CODE}/{$ARG_ENTITLEMENT_TYPE}",
            arguments = listOf(
                navArgument(ARG_CATEGORY_CODE) { type = NavType.StringType },
                navArgument(ARG_ENTITLEMENT_TYPE) { type = NavType.StringType }
            ),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) }
        ) {
            LicenceCategoryDetailRoute(
                onBack = onBack,
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToDvlaLinkIntro() {
    navigate(DVLA_LINK_INTRO_ROUTE)
}

fun NavController.navigateToDvlaLink() {
    navigate(DVLA_LINK_ROUTE)
}

fun NavController.navigateToVehicleDetails(vehicleId: Int) {
    navigate("$VEHICLE_DETAILS_ROUTE/$vehicleId")
}

fun NavController.navigateToVehicleDetailsByRegistration(regNumber: String) {
    navigate("$VEHICLE_SEARCH_RESULT_ROUTE/$regNumber") {
        popUpTo("$VEHICLE_SEARCH_RESULT_ROUTE/{$ARG_REG_NUMBER}") {
            inclusive = true
        }
    }
}

fun NavController.navigateToDrivingCategories() {
    navigate(LICENCE_CATEGORIES_ROUTE)
}

fun NavController.navigateToLicenceCategoryDetail(code: String, type: EntitlementType) {
    navigate("$LICENCE_CATEGORY_DETAIL_ROUTE/$code/${type.name}")
}
