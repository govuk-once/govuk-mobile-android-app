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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.parser.MarkdownParser
import uk.gov.govuk.chat.parser.model.InlineContent
import uk.gov.govuk.chat.parser.model.MarkdownElement
import uk.gov.govuk.design.ui.theme.GovUkTheme

private data class ContentSegment(
    val text: AnnotatedString,
    val linkUrl: String? = null
)

@Composable
internal fun Markdown(
    text: String,
    onMarkdownLinkClicked: (String, String) -> Unit,
    markdownLinkType: String,
    modifier: Modifier = Modifier
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
        elements.forEach { element ->
            MarkdownElementItem(
                element = element,
                onLinkClick = { url -> onMarkdownLinkClicked(markdownLinkType, url) },
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
private fun HeadingItem(
    heading: MarkdownElement.Heading,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String
) {
    val hasLinks = hasLinks(heading.content)
    val accessibilityText = if (hasLinks) {
        "${heading.plainText}. $linkAccessibilityLabel"
    } else null

    SegmentedText(
        content = heading.content,
        style = GovUkTheme.typography.bodyBold,
        onLinkClick = onLinkClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (accessibilityText != null) {
                    Modifier.semantics { contentDescription = accessibilityText }
                } else Modifier
            )
    )
}

@Composable
private fun ParagraphItem(
    paragraph: MarkdownElement.Paragraph,
    onLinkClick: (String) -> Unit,
    linkAccessibilityLabel: String
) {
    val hasLinks = hasLinks(paragraph.content)
    val accessibilityText = if (hasLinks) {
        "${paragraph.plainText}. $linkAccessibilityLabel"
    } else null

    SegmentedText(
        content = paragraph.content,
        style = GovUkTheme.typography.bodyRegular,
        onLinkClick = onLinkClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (accessibilityText != null) {
                    Modifier.semantics { contentDescription = accessibilityText }
                } else Modifier
            )
    )
}

@Composable
private fun CodeBlockItem(codeBlock: MarkdownElement.CodeBlock) {
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
    val hasLinks = hasLinks(blockQuote.content)
    val accessibilityText = if (hasLinks) {
        "${blockQuote.plainText}. $linkAccessibilityLabel"
    } else null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(
                if (accessibilityText != null) {
                    Modifier.semantics { contentDescription = accessibilityText }
                } else Modifier
            )
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
                modifier = Modifier.padding(12.dp)
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
    val indent = ((listItem.depth + 1) * 8).dp
    val bullet = if (listItem.isOrdered) {
        "${listItem.number}."
    } else {
        "\u2022"
    }

    val hasLinks = hasLinks(listItem.content)
    val accessibilityText = if (hasLinks) {
        "${listItem.plainText}. $linkAccessibilityLabel"
    } else {
        null
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
                .then(
                    if (accessibilityText != null) {
                        Modifier.semantics { contentDescription = accessibilityText }
                    } else Modifier
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
        links.size <= 1 -> {
            // 0 or 1 link: render as single Text element
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
                        modifier = if (segment.linkUrl != null) {
                            Modifier.clickable { onLinkClick(segment.linkUrl) }
                        } else {
                            Modifier
                        }
                    )
                }
            }
        }
    }
}

private fun hasLinks(content: List<InlineContent>): Boolean {
    return content.any { inline ->
        when (inline) {
            is InlineContent.Link -> true
            is InlineContent.Emphasis -> hasLinks(inline.content)
            is InlineContent.StrongEmphasis -> hasLinks(inline.content)
            else -> false
        }
    }
}

private fun collectLinks(content: List<InlineContent>): List<String> {
    val links = mutableListOf<String>()
    fun collect(items: List<InlineContent>) {
        for (item in items) {
            when (item) {
                is InlineContent.Link -> {
                    links.add(item.url)
                    collect(item.content)
                }
                is InlineContent.Emphasis -> collect(item.content)
                is InlineContent.StrongEmphasis -> collect(item.content)
                else -> {}
            }
        }
    }
    collect(content)
    return links
}

private fun appendInlineContent(
    items: List<InlineContent>,
    builder: AnnotatedString.Builder,
    inheritedStyles: List<SpanStyle>,
    linkColor: Color?,
    codeBackground: Color
) {
    for (item in items) {
        when (item) {
            is InlineContent.Text -> {
                inheritedStyles.forEach { builder.pushStyle(it) }
                builder.append(item.text)
                repeat(inheritedStyles.size) { builder.pop() }
            }
            is InlineContent.Code -> {
                val codeStyle = SpanStyle(fontFamily = FontFamily.Monospace, background = codeBackground)
                builder.pushStyle(codeStyle)
                inheritedStyles.forEach { builder.pushStyle(it) }
                builder.append(item.code)
                repeat(inheritedStyles.size + 1) { builder.pop() }
            }
            is InlineContent.Emphasis -> {
                builder.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                appendInlineContent(item.content, builder, inheritedStyles, linkColor, codeBackground)
                builder.pop()
            }
            is InlineContent.StrongEmphasis -> {
                builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendInlineContent(item.content, builder, inheritedStyles, linkColor, codeBackground)
                builder.pop()
            }
            is InlineContent.Link -> {
                if (linkColor != null) {
                    val linkStyle = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
                    builder.pushStyle(linkStyle)
                    appendInlineContent(item.content, builder, inheritedStyles, linkColor, codeBackground)
                    builder.pop()
                } else {
                    // Nested link - just render content without additional styling
                    appendInlineContent(item.content, builder, inheritedStyles, null, codeBackground)
                }
            }
            is InlineContent.LineBreak -> {
                builder.append(if (item.isSoft) " " else "\n")
            }
        }
    }
}

private fun splitIntoSegments(
    content: List<InlineContent>,
    linkColor: Color,
    codeBackground: Color
): List<ContentSegment> {
    val segments = mutableListOf<ContentSegment>()
    val currentText = StringBuilder()
    val currentStyles = mutableListOf<Pair<IntRange, SpanStyle>>()

    fun flushCurrentText() {
        if (currentText.isNotEmpty()) {
            val annotatedString = buildAnnotatedString {
                append(currentText.toString())
                currentStyles.forEach { (range, style) ->
                    addStyle(style, range.first, range.last.coerceAtMost(currentText.length))
                }
            }
            segments.add(ContentSegment(annotatedString))
            currentText.clear()
            currentStyles.clear()
        }
    }

    fun processContent(items: List<InlineContent>, inheritedStyles: List<SpanStyle> = emptyList()) {
        for (item in items) {
            when (item) {
                is InlineContent.Text -> {
                    val start = currentText.length
                    currentText.append(item.text)
                    val end = currentText.length
                    inheritedStyles.forEach { style ->
                        currentStyles.add(start until end to style)
                    }
                }

                is InlineContent.Code -> {
                    val start = currentText.length
                    currentText.append(item.code)
                    val end = currentText.length
                    val codeStyle = SpanStyle(fontFamily = FontFamily.Monospace, background = codeBackground)
                    currentStyles.add(start until end to codeStyle)
                    inheritedStyles.forEach { style ->
                        currentStyles.add(start until end to style)
                    }
                }

                is InlineContent.Emphasis -> {
                    val newStyles = inheritedStyles + SpanStyle(fontStyle = FontStyle.Italic)
                    processContent(item.content, newStyles)
                }

                is InlineContent.StrongEmphasis -> {
                    val newStyles = inheritedStyles + SpanStyle(fontWeight = FontWeight.Bold)
                    processContent(item.content, newStyles)
                }

                is InlineContent.Link -> {
                    // Flush any accumulated text before the link
                    flushCurrentText()

                    // Build the link text as its own segment
                    val linkText = buildAnnotatedString {
                        val linkStyle = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
                        withStyle(linkStyle) {
                            appendInlineContent(item.content, this@buildAnnotatedString, inheritedStyles, null, codeBackground)
                        }
                    }
                    segments.add(ContentSegment(linkText, item.url))
                }

                is InlineContent.LineBreak -> {
                    val start = currentText.length
                    currentText.append(if (item.isSoft) " " else "\n")
                    val end = currentText.length
                    inheritedStyles.forEach { style ->
                        currentStyles.add(start until end to style)
                    }
                }
            }
        }
    }

    processContent(content)
    flushCurrentText()

    return segments.ifEmpty { listOf(ContentSegment(AnnotatedString(""))) }
}

