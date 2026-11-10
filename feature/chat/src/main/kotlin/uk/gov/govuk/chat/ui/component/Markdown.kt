package uk.gov.govuk.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.markdown.MarkdownParser
import uk.gov.govuk.design.markdown.appendInlineContent
import uk.gov.govuk.design.markdown.collectLinks
import uk.gov.govuk.design.markdown.hasLinks
import uk.gov.govuk.design.markdown.model.InlineContent
import uk.gov.govuk.design.markdown.model.MarkdownElement
import uk.gov.govuk.design.markdown.splitIntoSegments
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Markdown(
    text: String,
    onMarkdownLinkClicked: (String, String) -> Unit,
    markdownLinkType: String,
    modifier: Modifier = Modifier,
    accessibilityPrefix: String? = null
) {
    val parser = remember { MarkdownParser() }
    val elements = remember(text) { parser.parse(text) }
    val linkAccessibilityLabel = stringResource(R.string.sources_open_in_text)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        elements.forEachIndexed { index, element ->
            MarkdownElementItem(
                element = element,
                onLinkClick = { url -> onMarkdownLinkClicked(markdownLinkType, url) },
                linkAccessibilityLabel = linkAccessibilityLabel,
                accessibilityPrefix = if (index == 0) accessibilityPrefix else null
            )
        }
    }
}

@Composable
private fun MarkdownElementItem(
    element: MarkdownElement,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String,
    accessibilityPrefix: String? = null
) {
    when (element) {
        is MarkdownElement.Heading -> HeadingItem(element, onLinkClick, linkAccessibilityLabel, accessibilityPrefix)
        is MarkdownElement.Paragraph -> ParagraphItem(element, onLinkClick, linkAccessibilityLabel, accessibilityPrefix)
        is MarkdownElement.CodeBlock -> CodeBlockItem(element, accessibilityPrefix)
        is MarkdownElement.BlockQuote -> BlockQuoteItem(element, onLinkClick, linkAccessibilityLabel, accessibilityPrefix)
        is MarkdownElement.ListItem -> ListItemItem(element, onLinkClick, linkAccessibilityLabel, accessibilityPrefix)
        is MarkdownElement.ThematicBreak -> ThematicBreakItem()
    }
}

@Composable
private fun HeadingItem(
    heading: MarkdownElement.Heading,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String,
    accessibilityPrefix: String? = null
) {
    SegmentedText(
        content = heading.content,
        style = GovUkTheme.typography.bodyBold,
        onLinkClick = onLinkClick,
        modifier = Modifier
            .fillMaxWidth()
            .withLinkAccessibility(
                heading.content,
                heading.plainText,
                linkAccessibilityLabel,
                accessibilityPrefix,
                isHeading = true
            )
    )
}

@Composable
private fun ParagraphItem(
    paragraph: MarkdownElement.Paragraph,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String,
    accessibilityPrefix: String? = null
) {
    SegmentedText(
        content = paragraph.content,
        style = GovUkTheme.typography.bodyRegular,
        onLinkClick = onLinkClick,
        modifier = Modifier
            .fillMaxWidth()
            .withLinkAccessibility(
                paragraph.content,
                paragraph.plainText,
                linkAccessibilityLabel,
                accessibilityPrefix
            )
    )
}

@Composable
private fun CodeBlockItem(
    codeBlock: MarkdownElement.CodeBlock,
    accessibilityPrefix: String? = null
) {
    val accessibilityText = accessibilityPrefix?.let { "$it ${codeBlock.plainText}" }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GovUkTheme.colourScheme.surfaces.chatIntroCardBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = codeBlock.code,
            style = GovUkTheme.typography.footnoteRegular.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier
                .padding(12.dp)
                .then(
                    if (accessibilityText != null) {
                        Modifier.semantics { contentDescription = accessibilityText }
                    } else Modifier
                )
        )
    }
}

@Composable
private fun BlockQuoteItem(
    blockQuote: MarkdownElement.BlockQuote,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String,
    accessibilityPrefix: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(GovUkTheme.colourScheme.textAndIcons.primary)
        )
        Surface(
            modifier = Modifier.weight(1f),
            color = GovUkTheme.colourScheme.surfaces.chatIntroCardBackground
        ) {
            SegmentedText(
                content = blockQuote.content,
                style = GovUkTheme.typography.bodyRegular.copy(
                    fontStyle = FontStyle.Italic
                ),
                onLinkClick = onLinkClick,
                modifier = Modifier
                    .padding(12.dp)
                    .withLinkAccessibility(
                        blockQuote.content,
                        blockQuote.plainText,
                        linkAccessibilityLabel,
                        accessibilityPrefix
                    )
            )
        }
    }
}

@Composable
private fun ListItemItem(
    listItem: MarkdownElement.ListItem,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String,
    accessibilityPrefix: String? = null
) {
    val indent = ((listItem.depth + 1) * 16).dp
    val bullet = if (listItem.isOrdered) {
        "${listItem.number}."
    } else {
        "\u2022"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent)
    ) {
        Text(
            text = bullet,
            style = GovUkTheme.typography.bodyRegular,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier
                .clearAndSetSemantics { }
        )
        SegmentedText(
            content = listItem.content,
            style = GovUkTheme.typography.bodyRegular,
            onLinkClick = onLinkClick,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .withLinkAccessibility(
                    listItem.content,
                    listItem.plainText,
                    linkAccessibilityLabel,
                    accessibilityPrefix
                )
        )
    }
}

@Composable
private fun ThematicBreakItem() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        thickness = 1.dp,
        color = GovUkTheme.colourScheme.strokes.listDivider
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SegmentedText(
    content: List<InlineContent>,
    style: TextStyle,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val linkColor = GovUkTheme.colourScheme.textAndIcons.chatBotLinkText
    val codeBackground = GovUkTheme.colourScheme.surfaces.background
    val links = remember(content) { collectLinks(content) }

    when {
        links.isEmpty() -> {
            // 0 links: render as single Text element
            val annotatedString = remember(content) {
                buildAnnotatedString {
                    appendInlineContent(content, this, emptyList(), linkColor, codeBackground)
                }
            }
            Text(
                text = annotatedString,
                style = style,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = if (links.size == 1) {
                    modifier.clickable { onLinkClick(links[0]) }
                } else {
                    modifier
                }
            )
        }
        else -> {
            // Multiple links: split into segments
            val segments = remember(content) {
                splitIntoSegments(content, linkColor, codeBackground)
            }
            FlowRow(
                modifier = modifier
            ) {
                segments.forEach { segment ->
                    Text(
                        text = segment.text,
                        style = style,
                        color = GovUkTheme.colourScheme.textAndIcons.primary,
                        modifier = segment.linkUrl?.let { Modifier.clickable { onLinkClick(it) } } ?: Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun Modifier.withLinkAccessibility(
    content: List<InlineContent>,
    plainText: String,
    linkAccessibilityLabel: String,
    accessibilityPrefix: String? = null,
    isHeading: Boolean = false
): Modifier {
    val hasLinks = hasLinks(content)
    val accessibilityText =
        buildString {
            accessibilityPrefix?.let { append("$it. ") }
            append(plainText)
            if (hasLinks) append(". $linkAccessibilityLabel")
        }.replace(
            stringResource(uk.gov.govuk.design.R.string.gov_uk),
            stringResource(uk.gov.govuk.design.R.string.gov_uk_alt_text)
        )

    return this.semantics {
        if (isHeading) heading()
        contentDescription = accessibilityText
    }
}

