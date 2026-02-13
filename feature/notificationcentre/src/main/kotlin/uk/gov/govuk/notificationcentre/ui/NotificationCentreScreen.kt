package uk.gov.govuk.notificationcentre.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.design.ui.theme.ThemePreviews
import uk.gov.govuk.notificationcentre.NotificationCentreViewModel

@Composable
internal fun NotificationCentreRoute(modifier: Modifier = Modifier) {
    val viewModel: NotificationCentreViewModel = hiltViewModel()

    Box(modifier.fillMaxSize()
        .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        NotificationCentreScreen({
            viewModel.onPageView()
        })
    }
}

@Composable
private fun NotificationCentreScreen(onPageView: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back({ })
        )
        BodyRegularLabel(text = "Notification Centre")
        LaunchedEffect(Unit) {
            onPageView()
        }
    }
}

@ThemePreviews
@Composable
private fun NotificationCentreScreenPreview() {
    GovUkTheme {
        NotificationCentreScreen({})
    }
}