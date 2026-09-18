package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CaptionRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.ToggleListItem
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.travelalerts.R
import uk.gov.govuk.travelalerts.data.model.Country

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CountryOptionsBottomSheet(
    country: Country,
    sheetState: SheetState,
    notificationsEnabled: Boolean,
    isTogglingNotifications: Boolean,
    isUnfollowing: Boolean,
    toggleError: String?,
    onDismiss: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onUnfollow: () -> Unit,
    onClearToggleError: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = if (!isTogglingNotifications && !isUnfollowing) onDismiss else ({} ),
        sheetState = sheetState,
        containerColor = GovUkTheme.colourScheme.surfaces.surfaceModal,
        contentColor = GovUkTheme.colourScheme.textAndIcons.primary,
        scrimColor = GovUkTheme.colourScheme.surfaces.primary.copy(alpha = 0.32f),
        properties = androidx.compose.material3.ModalBottomSheetProperties(
            shouldDismissOnBackPress = !isTogglingNotifications && !isUnfollowing
        )
    ) {
        CountryOptionsBottomSheetContent(
            country = country,
            notificationsEnabled = notificationsEnabled,
            isTogglingNotifications = isTogglingNotifications,
            isUnfollowing = isUnfollowing,
            onDismiss = onDismiss,
            onNotificationsToggle = onNotificationsToggle,
            onUnfollow = onUnfollow,
            toggleError = toggleError,
            onClearToggleError = onClearToggleError
        )
    }
}

@Composable
private fun CountryOptionsBottomSheetContent(
    country: Country,
    notificationsEnabled: Boolean,
    isTogglingNotifications: Boolean,
    isUnfollowing: Boolean,
    toggleError: String?,
    onDismiss: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onUnfollow: () -> Unit,
    onClearToggleError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = GovUkTheme.spacing.large)
    ) {
        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                enabled = !isTogglingNotifications && !isUnfollowing,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = country.name,
            style = GovUkTheme.typography.title2Bold,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        )

        SmallVerticalSpacer()

        ToggleListItem(
            title = stringResource(R.string.edit_countries_notifications),
            checked = notificationsEnabled,
            onCheckedChange = if (!isTogglingNotifications && !isUnfollowing) onNotificationsToggle else ({} ),
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
            backgroundOverride = GovUkTheme.colourScheme.surfaces.listAlt
        )

        if (isTogglingNotifications) {
            SmallVerticalSpacer()
            CaptionRegularLabel(
                text = stringResource(R.string.edit_countries_updating),
                color = GovUkTheme.colourScheme.textAndIcons.secondary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
            )
        }

        SmallVerticalSpacer()

        CardListItem(
            onClick = if (!isTogglingNotifications && !isUnfollowing) onUnfollow else null,
            background = GovUkTheme.colourScheme.surfaces.listAlt,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            if (isUnfollowing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = GovUkTheme.spacing.medium),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = GovUkTheme.colourScheme.surfaces.primary
                    )
                }
            } else {
                BodyRegularLabel(
                    text = stringResource(R.string.edit_countries_unfollow),
                    color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = GovUkTheme.spacing.medium)
                )
            }
        }

        MediumVerticalSpacer()

        CaptionRegularLabel(
            stringResource(R.string.edit_bottom_sheet_footer),
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
        )
    }

    if (toggleError != null) {
        AlertDialog(
            onDismissRequest = onClearToggleError,
            title = { Text(stringResource(R.string.edit_countries_error_title)) },
            text = { Text(stringResource(R.string.edit_countries_error_description)) },
            confirmButton = {
                Button(onClick = onClearToggleError) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@PreviewLightDark
private fun CountryOptionsBottomSheetContentPreview() {
    val country = Country("Bosnia and Herzegovina", "bosnia-and-herzegovina", "2022-01-01T00:00:00Z", listOf())

    GovUkTheme {
        CountryOptionsBottomSheetContent(
            country = country,
            notificationsEnabled = true,
            isTogglingNotifications = false,
            isUnfollowing = false,
            toggleError = null,
            onDismiss = {},
            onNotificationsToggle = {},
            onUnfollow = {},
            onClearToggleError = {}
        )
    }
}
