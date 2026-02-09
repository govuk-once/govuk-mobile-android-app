package uk.gov.govuk.chat.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.design.ui.theme.ThemePreviews

@Composable
fun ChatBanner(
    title: String,
    body: String,
    linkText: String,
    onClick: (String) -> Unit,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier
        .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .background(GovUkTheme.colourScheme.surfaces.list),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(start = GovUkTheme.spacing.medium)
                    ) {
                        MediumVerticalSpacer()
                        Title2BoldLabel(
                            title,
                            modifier = Modifier
                                .semantics { heading() },
                        )
                        SmallVerticalSpacer()
                        BodyRegularLabel(body)
                    }

                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.background_chat_banner),
                            contentDescription = null,
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.TopEnd)
                                .clickable { onDismiss(title) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(uk.gov.govuk.design.R.drawable.ic_cancel),
                                contentDescription = stringResource(R.string.chat_banner_dismiss_content_desc),
                                tint = GovUkTheme.colourScheme.textAndIcons.primary
                            )
                        }
                    }

                }

                MediumVerticalSpacer()
                HorizontalDivider(
                    thickness = 1.dp,
                    color = GovUkTheme.colourScheme.strokes.listDivider
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(linkText) }
                ) {
                    MediumVerticalSpacer()
                    Row(
                        modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BodyRegularLabel(
                            text = linkText,
                            modifier = Modifier
                                .weight(1f),
                            color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                        )
                        Icon(
                            painter = painterResource(uk.gov.govuk.design.R.drawable.ic_arrow),
                            contentDescription = null,
                            tint = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                        )
                    }
                    MediumVerticalSpacer()
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ChatBannerPreview() {
    GovUkTheme {
        ChatBanner(
            "Introduction GOV.UK Chat",
            "An experimental AI tool for finding quick answers",
            "Ask a question",
            { },
            { }
        )
    }
}