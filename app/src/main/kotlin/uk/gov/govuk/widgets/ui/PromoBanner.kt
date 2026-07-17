package uk.gov.govuk.widgets.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.R
import uk.gov.govuk.config.data.remote.model.PromoBanner
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun PromoBanner(
    promoBanner: PromoBanner,
    onClick: (text: String, url: String?) -> Unit,
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
                            promoBanner.title,
                            modifier = Modifier
                                .semantics { heading() },
                        )
                        SmallVerticalSpacer()
                        BodyRegularLabel(promoBanner.body)
                    }

                    Box {
                        if (promoBanner.image != null) {
                            DisplayImage(promoBanner.image!!)
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.TopEnd)
                                .clickable { onDismiss(promoBanner.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(uk.gov.govuk.design.R.drawable.ic_cancel),
                                contentDescription = stringResource(R.string.promo_banner_dismiss_content_desc),
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
                        .clickable {
                            onClick(promoBanner.link.title, promoBanner.link.url)
                        }
                ) {
                    MediumVerticalSpacer()
                    Row(
                        modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BodyRegularLabel(
                            text = promoBanner.link.title,
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

@Composable
private fun DisplayImage(image: String) {
    val context = LocalContext.current
    val imageResId = remember(image) {
        context.resources.getIdentifier(
            image,
            "drawable",
            context.packageName
        )
    }

    if (imageResId != 0) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
        )
    }
}
