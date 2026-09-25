package uk.gov.govuk.travelalerts.ui.countrylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedContainerDivider
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.travelalerts.R
import uk.gov.govuk.travelalerts.data.model.Country

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificationsPreferenceBottomSheet(
    country: Country,
    sheetState: SheetState,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onNotNow: () -> Unit,
    onGetNotifications: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = if (!isSaving) onDismiss else ({} ),
        sheetState = sheetState,
        containerColor = GovUkTheme.colourScheme.surfaces.surfaceModal,
        contentColor = GovUkTheme.colourScheme.textAndIcons.primary,
        scrimColor = GovUkTheme.colourScheme.surfaces.alertBackground,
        dragHandle = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                DragHandle()
            }
        },
        properties = androidx.compose.material3.ModalBottomSheetProperties(
            shouldDismissOnBackPress = !isSaving
        )
    ) {
        NotificationsPreferenceBottomSheetContent(
            country = country,
            isSaving = isSaving,
            onDismiss = onDismiss,
            onNotNow = onNotNow,
            onGetNotifications = onGetNotifications
        )
    }
}

@Composable
private fun NotificationsPreferenceBottomSheetContent(
    country: Country,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onNotNow: () -> Unit,
    onGetNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = GovUkTheme.spacing.medium
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    enabled = !isSaving,
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.iconSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Title3BoldLabel(
                text = stringResource(R.string.follow_country_notification_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(
                    R.string.follow_country_notification_body,
                    country.name
                ),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )

            MediumVerticalSpacer()
        }

        FixedContainerDivider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = GovUkTheme.spacing.large
                )
        ) {
            if (isSaving) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = GovUkTheme.colourScheme.surfaces.primary
                    )
                }
            } else {
                PrimaryButton(
                    text = stringResource(R.string.follow_country_notification_get),
                    onClick = onGetNotifications,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(GovUkTheme.spacing.medium))

            TextButton(
                onClick = onNotNow,
                enabled = !isSaving,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.follow_country_notification_not_now),
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary,
                    style = GovUkTheme.typography.bodyRegular
                )
            }
        }
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(top = 22.dp, bottom = 2.dp)
            .size(width = 32.dp, height = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(2.dp)
            )
            .semantics { contentDescription = "Bottom sheet drag handle" }
    )
}

@Composable
@PreviewLightDark
private fun NotificationsPreferenceBottomSheetContentPreview() {
    val country = Country("Bosnia and Herzegovina", "bosnia-and-herzegovina", "2022-01-01T00:00:00Z", listOf())

    GovUkTheme {
        NotificationsPreferenceBottomSheetContent(
            country = country,
            isSaving = false,
            onDismiss = {},
            onNotNow = {},
            onGetNotifications = {}
        )
    }
}

@Composable
@PreviewLightDark
private fun NotificationsPreferenceBottomSheetContentLoadingPreview() {
    val country = Country("Bosnia and Herzegovina", "bosnia-and-herzegovina", "2022-01-01T00:00:00Z", listOf())

    GovUkTheme {
        NotificationsPreferenceBottomSheetContent(
            country = country,
            isSaving = true,
            onDismiss = {},
            onNotNow = {},
            onGetNotifications = {}
        )
    }
}
