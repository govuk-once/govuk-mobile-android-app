package uk.gov.govuk.notificationcentre.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.FootnoteRegularLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notificationcentre.Notification
import uk.gov.govuk.notificationcentre.NotificationCentreDetailUiState
import uk.gov.govuk.notificationcentre.NotificationCentreDetailViewModel
import uk.gov.govuk.notificationcentre.NotificationCentreUiState
import uk.gov.govuk.notificationcentre.NotificationCentreViewModel
import uk.gov.govuk.notificationcentre.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun NotificationCentreDetailRoute(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val viewModel: NotificationCentreDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.chatBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChildPageHeader(
                dismissStyle = HeaderDismissStyle.Back(onClick = onBack)
            )

            Title(
                title = stringResource(R.string.notification_centre_title)
            )

            LargeVerticalSpacer()
            when (val state = uiState) {
                NotificationCentreDetailUiState.Loading -> Text("Loading")
                is NotificationCentreDetailUiState.Loaded -> Text("ID: ${state.notificationId}")
            }
            LaunchedEffect(Unit) {
                viewModel.onPageView()
            }
        }
    }
}