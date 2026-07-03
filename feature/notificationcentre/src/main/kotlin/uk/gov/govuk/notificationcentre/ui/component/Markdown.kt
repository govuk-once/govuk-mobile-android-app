package uk.gov.govuk.notificationcentre.ui.component

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
import uk.gov.govuk.notificationcentre.R
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
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val parser = remember { MarkdownParser() }
    val elements = remember(text) { parser.parse(text) }
    val linkAccessibilityLabel = stringResource(R.string.notification_markdown_link_opens_in_browser)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        elements.forEach { element ->
            MarkdownElementItem(
                element = element,
                onLinkClick = onLinkClick,
                linkAccessibilityLabel = linkAccessibilityLabel
            )
        }
    }
}

@Composable
private fun MarkdownElementItem(
    element: MarkdownElement,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String
) {
    when (element) {
        is MarkdownElement.Heading -> HeadingItem(element, onLinkClick, linkAccessibilityLabel)
        is MarkdownElement.Paragraph -> ParagraphItem(element, onLinkClick, linkAccessibilityLabel)
        is MarkdownElement.CodeBlock -> CodeBlockItem(element)
        is MarkdownElement.BlockQuote -> BlockQuoteItem(element, onLinkClick, linkAccessibilityLabel)
        is MarkdownElement.ListItem -> ListItemItem(element, onLinkClick, linkAccessibilityLabel)
        is MarkdownElement.ThematicBreak -> ThematicBreakItem()
    }
}

@Composable
private fun headingStyle(level: Int): TextStyle {
    return when (level) {
        1 -> GovUkTheme.typography.titleLargeBold
        2 -> GovUkTheme.typography.title1Bold
        3 -> GovUkTheme.typography.title2Bold
        4 -> GovUkTheme.typography.title3Bold
        5 -> GovUkTheme.typography.subheadlineBold
        else -> GovUkTheme.typography.captionBold
    }
}

@Composable
private fun HeadingItem(
    heading: MarkdownElement.Heading,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String
) {
    SegmentedText(
        content = heading.content,
        style = headingStyle(heading.level),
        onLinkClick = onLinkClick,
        modifier = Modifier
            .fillMaxWidth()
            .withLinkAccessibility(
                heading.content,
                heading.plainText,
                linkAccessibilityLabel,
                isHeading = true
            )
    )
}

@Composable
private fun ParagraphItem(
    paragraph: MarkdownElement.Paragraph,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String
) {
    SegmentedText(
        content = paragraph.content,
        style = GovUkTheme.typography.bodyRegular,
        onLinkClick = onLinkClick,
        modifier = Modifier
            .fillMaxWidth()
            .withLinkAccessibility(paragraph.content, paragraph.plainText, linkAccessibilityLabel)
    )
}

@Composable
private fun CodeBlockItem(codeBlock: MarkdownElement.CodeBlock) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GovUkTheme.colourScheme.surfaces.cardMsgHeader,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = codeBlock.code,
            style = GovUkTheme.typography.footnoteRegular.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun BlockQuoteItem(
    blockQuote: MarkdownElement.BlockQuote,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String
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
            color = GovUkTheme.colourScheme.surfaces.cardMsgHeader
        ) {
            SegmentedText(
                content = blockQuote.content,
                style = GovUkTheme.typography.bodyRegular.copy(fontStyle = FontStyle.Italic),
                onLinkClick = onLinkClick,
                modifier = Modifier
                    .padding(12.dp)
                    .withLinkAccessibility(blockQuote.content, blockQuote.plainText, linkAccessibilityLabel)
            )
        }
    }
}

@Composable
private fun ListItemItem(
    listItem: MarkdownElement.ListItem,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String
) {
    val indent = ((listItem.depth + 1) * 16).dp
    val bullet = if (listItem.isOrdered) "${listItem.number}." else "\u2022"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent)
    ) {
        Text(
            text = bullet,
            style = GovUkTheme.typography.bodyRegular,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.clearAndSetSemantics { }
        )
        SegmentedText(
            content = listItem.content,
            style = GovUkTheme.typography.bodyRegular,
            onLinkClick = onLinkClick,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .withLinkAccessibility(listItem.content, listItem.plainText, linkAccessibilityLabel)
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
    val linkColor = GovUkTheme.colourScheme.textAndIcons.linkPrimary
    val codeBackground = GovUkTheme.colourScheme.surfaces.background
    val links = remember(content) { collectLinks(content) }

    when {
        links.isEmpty() -> {
            val annotatedString = remember(content) {
                buildAnnotatedString {
                    appendInlineContent(content, this, emptyList(), linkColor, codeBackground)
                }
            }
            Text(
                text = annotatedString,
                style = style,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = modifier
            )
        }
        links.size == 1 -> {
            val annotatedString = remember(content) {
                buildAnnotatedString {
                    appendInlineContent(content, this, emptyList(), linkColor, codeBackground)
                }
            }
            Text(
                text = annotatedString,
                style = style,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = modifier.clickable { onLinkClick(links[0]) }
            )
        }
        else -> {
            val segments = remember(content) {
                splitIntoSegments(content, linkColor, codeBackground)
            }
            FlowRow(modifier = modifier) {
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
    isHeading: Boolean = false
): Modifier {
    val hasLinks = hasLinks(content)
    val accessibilityText =
        buildString {
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
