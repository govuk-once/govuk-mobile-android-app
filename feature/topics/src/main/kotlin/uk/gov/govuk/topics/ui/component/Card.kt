package uk.gov.govuk.topics.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.MediumHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R

@Composable
fun TopicSelectionCard(
    @DrawableRes icon: Int,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.listSelected
    } else {
        GovUkTheme.colourScheme.surfaces.listUnselected
    }

    val selected = stringResource(R.string.selected_alt_text)
    val notSelected = stringResource(R.string.not_selected_alt_text)

    val clickLabel = if (isSelected) {
        stringResource(R.string.deselect_alt_text)
    } else {
        stringResource(R.string.select_alt_text)
    }

    val topicAltText = "$title, ${if (isSelected) selected else notSelected}"

    Card(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                contentDescription = topicAltText
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClickLabel = clickLabel,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_selected),
                        contentDescription = null
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = GovUkTheme.colourScheme.textAndIcons.iconSurroundPrimary,
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.iconPrimary
                    )
                }
            }

            MediumHorizontalSpacer()

            val textColour = if (isSelected) {
                GovUkTheme.colourScheme.textAndIcons.listSelected
            } else {
                GovUkTheme.colourScheme.textAndIcons.listUnselected
            }

            BodyBoldLabel(
                text = title,
                color = textColour,
                modifier = Modifier.clearAndSetSemantics { } // announced in parent
            )
        }
    }
}

@Preview
@Composable
private fun TopicSelectionCardUnselectedPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = false,
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun TopicSelectionCardSelectedPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = true,
            onClick = { }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopicSelectionCardUnselectedDarkPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = false,
            onClick = { }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopicSelectionCardSelectedDarkPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = true,
            onClick = { }
        )
    }
}
