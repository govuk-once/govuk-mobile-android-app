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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        elements.forEach { element ->
            MarkdownElementItem(
                element = element,
                onLinkClick = { url -> onMarkdownLinkClicked(markdownLinkType, url) }
            )
        }
    }
}

@Composable
private fun MarkdownElementItem(
    element: MarkdownElement,
    onLinkClick: (String) -> Unit
) {
    when (element) {
        is MarkdownElement.Heading -> HeadingItem(element, onLinkClick)
        is MarkdownElement.Paragraph -> ParagraphItem(element, onLinkClick)
        is MarkdownElement.CodeBlock -> CodeBlockItem(element)
        is MarkdownElement.BlockQuote -> BlockQuoteItem(element, onLinkClick)
        is MarkdownElement.ListItem -> ListItemItem(element, onLinkClick)
        is MarkdownElement.ThematicBreak -> ThematicBreakItem()
    }
}

@Composable
private fun HeadingItem(
    heading: MarkdownElement.Heading,
    onLinkClick: (String) -> Unit
) {
    SegmentedText(
        content = heading.content,
        style = GovUkTheme.typography.bodyBold,
        onLinkClick = onLinkClick,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ParagraphItem(
    paragraph: MarkdownElement.Paragraph,
    onLinkClick: (String) -> Unit
) {
    SegmentedText(
        content = paragraph.content,
        style = GovUkTheme.typography.bodyRegular,
        onLinkClick = onLinkClick,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CodeBlockItem(codeBlock: MarkdownElement.CodeBlock) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GovUkTheme.colourScheme.surfaces.background
    ) {
        Text(
            text = codeBlock.code,
            style = GovUkTheme.typography.bodyRegular.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun BlockQuoteItem(
    blockQuote: MarkdownElement.BlockQuote,
    onLinkClick: (String) -> Unit
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
            color = GovUkTheme.colourScheme.surfaces.background
        ) {
            SegmentedText(
                content = blockQuote.content,
                style = GovUkTheme.typography.bodyRegular.copy(
                    fontStyle = FontStyle.Italic
                ),
                onLinkClick = onLinkClick,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun ListItemItem(
    listItem: MarkdownElement.ListItem,
    onLinkClick: (String) -> Unit
) {
    val indent = (listItem.depth * 16).dp
    val bullet = if (listItem.isOrdered) {
        "${listItem.number}."
    } else {
        "\u2022"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent)
            .semantics(mergeDescendants = true) {}
    ) {
        Text(
            text = bullet,
            style = GovUkTheme.typography.bodyRegular,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.width(20.dp)
        )
        SegmentedText(
            content = listItem.content,
            style = GovUkTheme.typography.bodyRegular,
            onLinkClick = onLinkClick,
            modifier = Modifier.weight(1f)
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
    val segments = remember(content) { splitIntoSegments(content, linkColor, codeBackground) }

    if (segments.size == 1 && segments[0].linkUrl == null) {
        // Simple case: no links, just render as a single Text
        Text(
            text = segments[0].text,
            style = style,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = modifier
        )
    } else {
        // Multiple segments or has links: use FlowRow
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
                            appendLinkContent(item.content, this@buildAnnotatedString, inheritedStyles, codeBackground)
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

private fun appendLinkContent(
    content: List<InlineContent>,
    builder: AnnotatedString.Builder,
    inheritedStyles: List<SpanStyle>,
    codeBackground: Color
) {
    for (item in content) {
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
                appendLinkContent(item.content, builder, inheritedStyles, codeBackground)
                builder.pop()
            }

            is InlineContent.StrongEmphasis -> {
                builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendLinkContent(item.content, builder, inheritedStyles, codeBackground)
                builder.pop()
            }

            is InlineContent.Link -> {
                // Nested links - just render the content
                appendLinkContent(item.content, builder, inheritedStyles, codeBackground)
            }

            is InlineContent.LineBreak -> {
                builder.append(if (item.isSoft) " " else "\n")
            }
        }
    }
}
