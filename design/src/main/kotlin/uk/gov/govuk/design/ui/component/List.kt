package uk.gov.govuk.design.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.model.IconListItemStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun InternalLinkListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    style: InternalLinkListItemStyle = InternalLinkListItemStyle.Default
) {
    CardListItem(
        modifier = modifier,
        onClick = onClick,
        isFirst = isFirst,
        isLast = isLast
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
                BodyRegularLabel(
                    text = title,
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
                description?.let { description ->
                    ExtraSmallVerticalSpacer()
                    SubheadlineRegularLabel(
                        text = description,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }

            MediumHorizontalSpacer()

            when (style) {
                is InternalLinkListItemStyle.Status -> {
                    BodyRegularLabel(
                        text = style.title,
                        color = GovUkTheme.colourScheme.textAndIcons.iconTertiary
                    )
                }

                else -> {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun ExternalLinkListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    style: ExternalLinkListItemStyle = ExternalLinkListItemStyle.Default
) {
    CardListItem(
        modifier = modifier,
        onClick = onClick,
        isFirst = isFirst,
        isLast = isLast
    ) {
        val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)
        Row(
            modifier = Modifier
                .padding(all = GovUkTheme.spacing.medium)
                .fillMaxWidth()
                .talkBackText(title, description, opensInWebBrowser)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BodyRegularLabel(
                    text = title,
                    color = GovUkTheme.colourScheme.textAndIcons.link
                )

                description?.let { description ->
                    ExtraSmallVerticalSpacer()

                    SubheadlineRegularLabel(
                        text = description,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }

            when (style) {
                is ExternalLinkListItemStyle.Icon -> {
                    Icon(
                        painter = painterResource(R.drawable.ic_external_link),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.link
                    )
                }

                is ExternalLinkListItemStyle.Button -> {
                    TextButton(
                        onClick = style.onClick,
                        modifier = Modifier
                            .semantics { contentDescription = style.altText }
                            .align(Alignment.CenterVertically),
                        contentPadding = PaddingValues(start = GovUkTheme.spacing.extraLarge)
                    ) {
                        Icon(
                            painter = painterResource(style.icon),
                            contentDescription = null,
                            tint = GovUkTheme.colourScheme.textAndIcons.secondary
                        )
                    }
                }
                else -> { /* Do nothing */ }
            }
        }
    }
}

@Composable
fun ToggleListItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = true,
    isLast: Boolean = true
) {

    val status = stringResource(if (checked) R.string.on_button else R.string.off_button)
    val altText = "$title, $status"

    CardListItem(
        modifier = modifier,
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(vertical = GovUkTheme.spacing.small)
                .clickable(
                    onClick = { onCheckedChange(!checked) },
                    onClickLabel = stringResource(R.string.action_toggle)
                )
                .semantics(mergeDescendants = true) {
                    contentDescription = altText
                    role = Role.Switch
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .clearAndSetSemantics { }
            )

            MediumHorizontalSpacer()

            ToggleSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                testDescription = title,
                Modifier.clearAndSetSemantics { }
            )
        }
    }
}

@Composable
fun IconListItem(
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: IconListItemStyle = IconListItemStyle.Regular,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    altText: String? = null
) {
    CardListItem(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        isFirst = isFirst,
        isLast = isLast,
        drawDivider = false
    ) {
        Box(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = GovUkTheme.spacing.medium)
                    .semantics {
                        altText?.let {
                            contentDescription = it
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconConfig = when (style) {
                    IconListItemStyle.Regular ->
                        Pair(
                            GovUkTheme.colourScheme.textAndIcons.iconSurroundSecondary,
                            GovUkTheme.colourScheme.textAndIcons.iconSecondary
                        )
                    IconListItemStyle.Bold ->
                        Pair(
                            GovUkTheme.colourScheme.textAndIcons.iconSurroundPrimary,
                            GovUkTheme.colourScheme.textAndIcons.iconPrimary
                        )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = iconConfig.first,
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = iconConfig.second
                    )
                }

                MediumHorizontalSpacer()

                when (style) {
                    IconListItemStyle.Regular -> {
                        BodyRegularLabel(
                            text = title,
                            modifier = Modifier.weight(1f),
                            color = GovUkTheme.colourScheme.textAndIcons.linkPrimary
                        )
                    }
                    IconListItemStyle.Bold -> {
                        BodyBoldLabel(
                            text = title,
                            modifier = Modifier.weight(1f)
                        )

                        MediumHorizontalSpacer()

                        Icon(
                            painter = painterResource(R.drawable.ic_arrow),
                            contentDescription = null,
                            tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary
                        )
                    }
                }

            }
        }

        if (!isLast) {
            ListDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 72.dp)
                    .padding(end = GovUkTheme.spacing.medium)
            )
        }
    }
}

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    drawDivider: Boolean = true,
    content: @Composable () -> Unit,
) {
    val cornerRadius = 12.dp
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (isFirst) cornerRadius else 0.dp,
                    topEnd = if (isFirst) cornerRadius else 0.dp,
                    bottomStart = if (isLast) cornerRadius else 0.dp,
                    bottomEnd = if (isLast) cornerRadius else 0.dp
                )
            )
            .background(GovUkTheme.colourScheme.surfaces.list)
            .then(
                onClick?.let {
                    Modifier.clickable { it() }
                } ?: Modifier
            )
    ) {
        content()

        if (!isLast && drawDivider) {
            ListDivider(Modifier.padding(horizontal = GovUkTheme.spacing.medium))
        }
    }
}

@Preview
@Composable
private fun InternalLinkListItemPreview() {
    GovUkTheme {
        InternalLinkListItem("Title", {})
    }
}

@Preview
@Composable
private fun InternalLinkListItemDescriptionPreview() {
    GovUkTheme {
        InternalLinkListItem("Title", {}, description = "Description")
    }
}

@Preview
@Composable
private fun InternalLinkListItemStatusPreview() {
    GovUkTheme {
        InternalLinkListItem("Title", {}, style = InternalLinkListItemStyle.Status("Status"))
    }
}

@Preview
@Composable
private fun ExternalLinkListItemDefaultPreview() {
    GovUkTheme {
        ExternalLinkListItem("Title", {})
    }
}

@Preview
@Composable
private fun ExternalLinkListItemIconPreview() {
    GovUkTheme {
        ExternalLinkListItem(
            "Title",
            {},
            description = "Description",
            style = ExternalLinkListItemStyle.Icon
        )
    }
}

@Preview
@Composable
private fun ExternalLinkListItemButtonPreview() {
    GovUkTheme {
        ExternalLinkListItem(
            "Title", {}, description = "Description",
            style = ExternalLinkListItemStyle.Button(R.drawable.ic_cancel_round, "Alt text") {})
    }
}
