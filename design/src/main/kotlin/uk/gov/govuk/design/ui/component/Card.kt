package uk.gov.govuk.design.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.extension.drawBottomStroke
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.model.CardListItem
import uk.gov.govuk.design.ui.model.EmergencyBannerUiType
import uk.gov.govuk.design.ui.model.FocusableCardColours
import uk.gov.govuk.design.ui.model.backgroundColour
import uk.gov.govuk.design.ui.model.borderColour
import uk.gov.govuk.design.ui.model.dismissIconColour
import uk.gov.govuk.design.ui.model.hasDecoratedLink
import uk.gov.govuk.design.ui.model.linkTitleColour
import uk.gov.govuk.design.ui.model.textColour
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.design.ui.theme.ThemePreviews

@Composable
fun GovUkOutlinedCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.cardBlue,
    borderColour: Color = GovUkTheme.colourScheme.strokes.cardBlue,
    padding: Dp = GovUkTheme.spacing.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColour = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.listSelected
    } else {
        backgroundColour
    }

    val strokeColour = if (isSelected) {
        GovUkTheme.colourScheme.strokes.cardSelected
    } else {
        borderColour
    }

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColour),
        border = BorderStroke(
            width = 1.dp,
            color = strokeColour
        )
    ) {
        Column(
            modifier = Modifier
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)  // if onClick is null Talkback announces 'disabled'
                .padding(padding)
        ) {
            content()
        }
    }
}

@Composable
fun HomeBannerCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    linkTitle: String?,
    isDismissible: Boolean = true,
    dismissAltText: String? = null,
    type: EmergencyBannerUiType,
    onClick: (() -> Unit)? = null,
    onSuppressClick: (() -> Unit)? = null
) {
    val backgroundColour = type.backgroundColour
    val borderColour = type.borderColour
    val textColour = type.textColour
    val dismissIconColour = type.dismissIconColour
    val linkTitleColour = type.linkTitleColour
    val showDivider = type.hasDecoratedLink
    val dividerColour = GovUkTheme.colourScheme.strokes.cardEmergencyBannerDivider

    GovUkOutlinedCard(
        modifier = modifier,
        backgroundColour = backgroundColour,
        borderColour = borderColour,
        padding = 0.dp
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                    ) {
                        MediumVerticalSpacer()
                        title?.let { title ->
                            BodyBoldLabel(
                                title,
                                color = textColour,
                                modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
                                    .semantics { heading() },
                            )
                        }

                        if (title != null && description != null) {
                            SmallVerticalSpacer()
                        }

                        description?.let { description ->
                            BodyRegularLabel(
                                description,
                                color = textColour,
                                modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
                            )
                        }
                        MediumVerticalSpacer()
                    }
                }
            }

            // dismiss Button Logic
            onSuppressClick?.let {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = onSuppressClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDismissible) {
                        Icon(
                            painterResource(R.drawable.ic_cancel),
                            contentDescription = dismissAltText ?: "${stringResource(R.string.content_desc_remove)} ${title ?: ""}",
                            tint = dismissIconColour
                        )
                    }
                }
            }
        }

        // link section
        if (linkTitle != null && onClick != null) {
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {
                Column {
                    if (showDivider) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = dividerColour
                        )
                        MediumVerticalSpacer()
                    }

                    val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onClick)
                            .padding(horizontal = GovUkTheme.spacing.medium)
                            .semantics {
                                contentDescription = "$linkTitle $opensInWebBrowser"
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BodyRegularLabel(
                            text = linkTitle,
                            color = linkTitleColour,
                            modifier = Modifier.weight(1f)
                                .clearAndSetSemantics{ }
                        )
                        if (type.hasDecoratedLink) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow),
                                contentDescription = null,
                                tint = GovUkTheme.colourScheme.textAndIcons.linkInverse,
                                modifier = Modifier.padding(start = GovUkTheme.spacing.small)
                            )
                        }
                    }

                    MediumVerticalSpacer()
                }
            }
        }
    }
}
@Composable
fun SearchResultCard(
    title: String,
    description: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkOutlinedCard(
        modifier = modifier,
        onClick = onClick

    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )

            Icon(
                painter = painterResource(
                    R.drawable.ic_external_link
                ),
                contentDescription = stringResource(
                    R.string.opens_in_web_browser
                ),
                tint = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
            )
        }

        if (!description.isNullOrBlank()) {
            SmallVerticalSpacer()
            BodyRegularLabel(description)
        }
    }
}

@Composable
fun UserFeedbackCard(
    body: String,
    linkTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BodyRegularLabel(
            text = body,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            textAlign = TextAlign.Center
        )
        val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)
        BodyRegularLabel(
            text = linkTitle,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = GovUkTheme.spacing.medium)
                .clickable(onClick = onClick)
                .semantics {
                    contentDescription = "$linkTitle $opensInWebBrowser"
                },
            color = GovUkTheme.colourScheme.textAndIcons.linkSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NonTappableCard(
    body: String,
    modifier: Modifier = Modifier
) {
    BodyRegularLabel(
        text = body,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList))
            .background(GovUkTheme.colourScheme.surfaces.cardNonTappable)
            .padding(GovUkTheme.spacing.medium),
        color = GovUkTheme.colourScheme.textAndIcons.secondary
    )
}

@Composable
fun CentredCardWithIcon(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .talkBackText(title, description)
            .drawBottomStroke(
                colour = GovUkTheme.colourScheme.strokes.cardDefault,
                cornerRadius = GovUkTheme.numbers.cornerAndroidList
            ),
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.list)
    ) {
        CentredContentWithIcon(
            icon = icon,
            modifier = Modifier
                .clickable(onClick = onClick),
            title = title,
            description = description
        )
    }
}

@Composable
fun CentredContentWithIcon(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .talkBackText(title, description)
    ) {
        ExtraLargeVerticalSpacer()

        Icon(
            painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(32.dp),
            tint = GovUkTheme.colourScheme.textAndIcons.icon
        )

        title?.let { title ->
            SmallVerticalSpacer()

            BodyBoldLabel(
                text = title,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics { }
                    .padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center,
            )
        }

        description?.let { description ->
            SmallVerticalSpacer()

            BodyRegularLabel(
                text = description,
                color = GovUkTheme.colourScheme.textAndIcons.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics { }
                    .padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center,
            )
        }

        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun NavigationCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .talkBackText(title, description, opensInWebBrowser)
            .drawBottomStroke(
                colour = GovUkTheme.colourScheme.strokes.cardDefault,
                cornerRadius = GovUkTheme.numbers.cornerAndroidList
            ),
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.list),
        onClick = onClick,
        content = {
            MediumVerticalSpacer()

            description?.let { description ->
                BodyRegularLabel(
                    text = description,
                    color = GovUkTheme.colourScheme.textAndIcons.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearAndSetSemantics { }
                        .padding(horizontal = GovUkTheme.spacing.medium)
                )

                SmallVerticalSpacer()
            }

            Title2BoldLabel(
                text = title,
                color = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics { }
                    .padding(horizontal = GovUkTheme.spacing.medium)
            )

            MediumVerticalSpacer()
        }
    )
}

@Composable
fun FocusableCard(
    item: CardListItem,
    modifier: Modifier = Modifier,
    colourMapper: @Composable (FocusableCardColours) -> Color
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val backgroundColor = if (isFocused) {
        colourMapper(FocusableCardColours.Focussed.Background)
    } else {
        colourMapper(FocusableCardColours.UnFocussed.Background)
    }

    val contentColor = if (isFocused) {
        colourMapper(FocusableCardColours.Focussed.Content)
    } else {
        colourMapper(FocusableCardColours.UnFocussed.Content)
    }

    Card(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .fillMaxWidth()
            .talkBackText(
                item.title,
                stringResource(R.string.opens_in_web_browser)
            )
            .drawBottomStroke(
                colour = GovUkTheme.colourScheme.strokes.cardCarousel,
                cornerRadius = GovUkTheme.numbers.cornerAndroidList
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        onClick = item.onClick,
        content = {
            MediumVerticalSpacer()
            SubheadlineBoldLabel(
                text = item.title,
                color = contentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) { }
                    .padding(horizontal = GovUkTheme.spacing.medium)
            )
            MediumVerticalSpacer()
        }
    )
}

@Composable
fun DrillInCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Card(
        modifier = modifier.drawBottomStroke(
            colour = GovUkTheme.colourScheme.strokes.cardDefault,
            cornerRadius = GovUkTheme.numbers.cornerAndroidList
        ),
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.cardDefault),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(all = GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BodyBoldLabel(
                    text = title
                )
                description?.let { description ->
                    SmallVerticalSpacer()
                    BodyRegularLabel(
                        text = description,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }

            MediumHorizontalSpacer()
            Icon(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary
            )
        }
    }
}

@ThemePreviews
@Composable
private fun HomeNotableDeathBannerCardPreview() {
    GovUkTheme {
        HomeBannerCard(
            title = "His Majesty King Henry VIII",
            description = "1491 to 1547",
            linkTitle = "A link description",
            isDismissible = true,
            type = EmergencyBannerUiType.NOTABLE_DEATH,
            onSuppressClick = { }
        )
    }
}

@ThemePreviews
@Composable
private fun HomeNationalEmergencyBannerCardPreview() {
    GovUkTheme {
        HomeBannerCard(
            title = "National emergency",
            description = "This is a level 1 incident",
            linkTitle = "A link description",
            isDismissible = true,
            type = EmergencyBannerUiType.NATIONAL_EMERGENCY,
            onSuppressClick = { }
        )
    }
}

@ThemePreviews
@Composable
private fun HomeLocalEmergencyBannerCardPreview() {
    GovUkTheme {
        HomeBannerCard(
            title = "Local emergency",
            description = "This is a level 2 incident",
            linkTitle = "A link description",
            isDismissible = true,
            type = EmergencyBannerUiType.LOCAL_EMERGENCY,
            onSuppressClick = { }
        )
    }
}

@ThemePreviews
@Composable
private fun HomeInformationEmergencyBannerCardPreview() {
    GovUkTheme {
        HomeBannerCard(
            title = "Emergency alerts",
            description = "Test on Sunday 7 September, 3pm",
            linkTitle = "A link description",
            isDismissible = true,
            type = EmergencyBannerUiType.INFORMATION,
            onSuppressClick = { }
        )
    }
}

@Preview
@Composable
private fun SearchResultWithDescriptionPreview() {
    GovUkTheme {
        SearchResultCard(
            title = "Card title",
            description = "Description",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun SearchResultWithoutDescriptionPreview() {
    GovUkTheme {
        SearchResultCard(
            title = "Card title",
            description = null,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun UserFeedbackCardPreview() {
    GovUkTheme {
        UserFeedbackCard("Card body", "A link description", {})
    }
}

@Preview
@Composable
private fun NonTappableCardPreview() {
    GovUkTheme {
        NonTappableCard("Card body")
    }
}

@Preview
@Composable
private fun CentredCardWithIconWithTitleAndDescriptionPreview() {
    GovUkTheme {
        CentredCardWithIcon(
            onClick = { },
            icon = R.drawable.ic_settings,
            title = "Card title",
            description = "Card secondary text that may go over multiple lines."
        )
    }
}

@Preview
@Composable
private fun CentredCardWithIconWithTitlePreview() {
    GovUkTheme {
        CentredCardWithIcon(
            onClick = { },
            icon = R.drawable.ic_settings,
            title = "Card title"
        )
    }
}

@Preview
@Composable
private fun CentredCardWithIconWithDescriptionPreview() {
    GovUkTheme {
        CentredCardWithIcon(
            onClick = { },
            icon = R.drawable.ic_settings,
            description = "Card secondary text that may go over multiple lines."
        )
    }
}

@Preview
@Composable
private fun NavigationCardWithTitleAndDescriptionPreview() {
    GovUkTheme {
        NavigationCard(
            title = "Card title",
            description = "Card secondary text that may go over multiple lines.",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun NavigationCardWithoutDescriptionPreview() {
    GovUkTheme {
        NavigationCard(
            title = "Card title",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun DrillInCardPreview() {
    GovUkTheme {
        DrillInCard(
            title = "Card title",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun DrillInCardDescriptionPreview() {
    GovUkTheme {
        DrillInCard(
            title = "Card title",
            onClick = {},
            description = "Card description"
        )
    }
}
