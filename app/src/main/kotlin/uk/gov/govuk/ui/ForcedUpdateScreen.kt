package uk.gov.govuk.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ForcedUpdateScreen(
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .safeDrawingPadding()
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(
                text = stringResource(R.string.forced_update_title),
                modifier = Modifier.semantics { heading() }
            )
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.forced_update_description))
        }

        val text = stringResource(R.string.forced_update_button_title_update)
        FixedPrimaryButton(
            text = text,
            onClick = onUpdateClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForcedUpdatePreview() {
    GovUkTheme {
        ForcedUpdateScreen(
            onUpdateClick = {},
            Modifier
        )
    }
}
