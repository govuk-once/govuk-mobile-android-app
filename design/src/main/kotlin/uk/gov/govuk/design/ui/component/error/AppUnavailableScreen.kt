package uk.gov.govuk.design.ui.component.error

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedSecondaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.error.ErrorConstants.GOV_UK_URL
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun AppUnavailableScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(
                text = stringResource(R.string.app_unavailable_title),
                modifier = Modifier.semantics { heading() }
            )
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.app_unavailable_description))
        }

        val context = LocalContext.current
        val text = stringResource(R.string.go_to_the_gov_uk_website)
        FixedSecondaryButton(
            text = text,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = GOV_UK_URL.toUri()
                context.startActivity(intent)
            },
            externalLink = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppUnavailablePreview() {
    GovUkTheme {
        AppUnavailableScreen(
            Modifier
        )
    }
}
