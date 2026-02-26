package uk.gov.govuk.ui

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
import uk.gov.govuk.BuildConfig.PLAY_STORE_URL
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun RecommendUpdateScreen(
    recommendUpdateSkipped: () -> Unit,
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
                text = stringResource(R.string.recommend_update_title),
                modifier = Modifier.semantics { heading() }
            )
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.recommend_update_description))
        }

        val updateButtonText = stringResource(R.string.recommend_update_button_title_update)
        val skipButtonText = stringResource(R.string.recommend_update_button_title_skip)

        val context = LocalContext.current
        val onUpdateClick: () -> Unit = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = PLAY_STORE_URL.toUri()
            context.startActivity(intent)
        }

        FixedDoubleButtonGroup(
            primaryText = updateButtonText,
            onPrimary = onUpdateClick,
            secondaryText = skipButtonText,
            onSecondary = recommendUpdateSkipped
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecommendUpdatePreview() {
    GovUkTheme {
        RecommendUpdateScreen(
            recommendUpdateSkipped = {},
            Modifier
        )
    }
}
