package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun PrivacyPolicyLink(
    modifier: Modifier = Modifier,
    onClick: ((text: String, url: String) -> Unit)? = null
) {
    val text = stringResource(R.string.privacy_policy_read_more)
    val url = PRIVACY_POLICY_URL
    Row(
        modifier
            .clickable {
                onClick?.invoke(text, url)
            }
    ) {
        BodyRegularLabel(
            text = text,
            color = GovUkTheme.colourScheme.textAndIcons.link,
            modifier = Modifier.weight(1f, fill = false)
        )
        SmallHorizontalSpacer()
        Icon(
            painter = painterResource(
                R.drawable.ic_external_link
            ),
            contentDescription = stringResource(R.string.opens_in_web_browser),
            tint = GovUkTheme.colourScheme.textAndIcons.link,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
