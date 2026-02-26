package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Sources(
    sources: List<String>,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val degrees by animateFloatAsState(if (expanded) 0f else -180f)

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ChatDivider()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = GovUkTheme.spacing.small
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_info_24),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.icon,
                modifier = Modifier
                    .padding(end = GovUkTheme.spacing.small),
            )

            BodyBoldLabel(
                text = stringResource(id = R.string.bot_sources_header_text)
            )
        }

        Row(
            modifier = Modifier
                .clickable {
                    expanded = !expanded
                    if (expanded) {
                        onSourcesExpanded()
                    }
                }
                .fillMaxWidth()
                .padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = GovUkTheme.spacing.small
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyRegularLabel(
                text = stringResource(id = R.string.bot_sources_list_description)
            )

            Image(
                painter = painterResource(R.drawable.outline_arrow_drop_up_24),
                contentDescription = null,
                modifier = Modifier.rotate(degrees),
                colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.icon)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                sources.forEachIndexed { index, _ ->
                    val linkAddendumText = stringResource(id = R.string.sources_open_in_text)
                    val linkText = "${sources[index]} $linkAddendumText"

                    SmallVerticalSpacer()

                    Markdown(
                        text = sources[index],
                        talkbackText = linkText,
                        onMarkdownLinkClicked = onMarkdownLinkClicked,
                        markdownLinkType = Analytics.RESPONSE_SOURCE_LINK_CLICKED
                    )

                    if (index < sources.size - 1) {
                        SmallVerticalSpacer()
                        ChatDivider(
                            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                        )
                    }
                }

                MediumVerticalSpacer()
            }
        }
    }
}

@Composable
private fun ChatDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        thickness = 1.dp,
        color = GovUkTheme.colourScheme.strokes.chatDivider,
        modifier = modifier
    )
}
