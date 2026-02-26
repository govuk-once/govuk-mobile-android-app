package uk.gov.govuk.notifications.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notifications.R

@Composable
internal fun NotificationsSettingsAlert(
    onContinueButtonClick: (String) -> Unit,
    onCancelButtonClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    val alertTitle =
        stringResource(
            id = if (notificationsEnabled)
                R.string.notifications_alert_title_off
            else
                R.string.notifications_alert_title_on
        )
    val alertMessage =
        stringResource(
            id = if (notificationsEnabled)
                R.string.notifications_alert_message_off
            else
                R.string.notifications_alert_message_on
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        text = {
            Column {
                BodyBoldLabel(
                    text = alertTitle,
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
                MediumVerticalSpacer()
                BodyRegularLabel(
                    text = alertMessage,
                    color = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            }
        },
        confirmButton = {
            val continueButton = stringResource(id = R.string.continue_button)
            TextButton(
                onClick = {
                    onContinueButtonClick(continueButton)
                    openDeviceNotificationsSettings(context)
                    onDismiss()
                }
            ) {
                BodyBoldLabel(
                    text = continueButton,
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                )
            }
        },
        dismissButton = {
            val cancelButton = stringResource(id = R.string.cancel_button)
            TextButton(
                onClick = {
                    onCancelButtonClick(cancelButton)
                    onDismiss()
                }
            ) {
                BodyRegularLabel(
                    text = cancelButton,
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                )
            }
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}

private fun openDeviceNotificationsSettings(
    context: Context
) {
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }.also { intent ->
        context.startActivity(intent)
    }
}
