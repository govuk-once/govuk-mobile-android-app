package uk.gov.govuk.design.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun InfoAlert(
    @StringRes title: Int,
    @StringRes message: Int,
    @StringRes buttonText: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        title = {
            BodyBoldLabel(
                text = stringResource(id = title),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        },
        text = {
            BodyRegularLabel(
                text = stringResource(id = message),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        },
        confirmButton = {
            DialogButton(
                text = stringResource(id = buttonText),
                onClick = onDismiss,
                isBold = true,
                defaultTextColour = GovUkTheme.colourScheme.textAndIcons.linkSecondary
            )
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}

@Preview(showBackground = true)
@Composable
private fun InfoAlertPreview() {
    GovUkTheme {
        InfoAlert(
            title = R.string.problem_title,
            message = R.string.problem_description,
            buttonText = R.string.try_again,
            onDismiss = {}
        )
    }
}
