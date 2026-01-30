package uk.gov.govuk.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    val firstLink = findFirstLink(heading.content)

    ClickableText(
        annotatedString = buildInlineAnnotatedString(heading.content),
        style = GovUkTheme.typography.bodyBold,
        firstLinkUrl = firstLink,
        onLinkClick = onLinkClick,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ParagraphItem(
    paragraph: MarkdownElement.Paragraph,
    onLinkClick: (String) -> Unit
) {
    val firstLink = findFirstLink(paragraph.content)

    ClickableText(
        annotatedString = buildInlineAnnotatedString(paragraph.content),
        style = GovUkTheme.typography.bodyRegular,
        firstLinkUrl = firstLink,
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
    val firstLink = findFirstLink(blockQuote.content)

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
            ClickableText(
                annotatedString = buildInlineAnnotatedString(blockQuote.content),
                style = GovUkTheme.typography.bodyRegular.copy(
                    fontStyle = FontStyle.Italic
                ),
                firstLinkUrl = firstLink,
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
    val firstLink = findFirstLink(listItem.content)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent)
            .then(
                if (firstLink != null) {
                    Modifier.clickable { onLinkClick(firstLink) }
                } else {
                    Modifier
                }
            )
            .semantics(mergeDescendants = true) {}
    ) {
        Text(
            text = bullet,
            style = GovUkTheme.typography.bodyRegular,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.width(20.dp)
        )
        Text(
            text = buildInlineAnnotatedString(listItem.content),
            style = GovUkTheme.typography.bodyRegular,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
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

@Composable
private fun ClickableText(
    annotatedString: AnnotatedString,
    style: TextStyle,
    firstLinkUrl: String?,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = annotatedString,
        style = style,
        color = GovUkTheme.colourScheme.textAndIcons.primary,
        modifier = modifier.then(
            if (firstLinkUrl != null) {
                Modifier.clickable { onLinkClick(firstLinkUrl) }
            } else {
                Modifier
            }
        )
    )
}

private fun findFirstLink(content: List<InlineContent>): String? {
    for (inline in content) {
        when (inline) {
            is InlineContent.Link -> return inline.url
            is InlineContent.Emphasis -> findFirstLink(inline.content)?.let { return it }
            is InlineContent.StrongEmphasis -> findFirstLink(inline.content)?.let { return it }
            else -> { /* continue */ }
        }
    }
    return null
}

@Composable
private fun buildInlineAnnotatedString(
    content: List<InlineContent>
): AnnotatedString {
    val linkColor = GovUkTheme.colourScheme.textAndIcons.chatBotLinkText
    val codeBackground = GovUkTheme.colourScheme.surfaces.background

    return buildAnnotatedString {
        content.forEach { inline ->
            appendInlineContent(inline, this, linkColor, codeBackground)
        }
    }
}

private fun appendInlineContent(
    inline: InlineContent,
    builder: AnnotatedString.Builder,
    linkColor: androidx.compose.ui.graphics.Color,
    codeBackground: androidx.compose.ui.graphics.Color
) {
    when (inline) {
        is InlineContent.Text -> builder.append(inline.text)

        is InlineContent.Code -> {
            builder.withStyle(
                SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    background = codeBackground
                )
            ) {
                append(inline.code)
            }
        }

        is InlineContent.Emphasis -> {
            builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                inline.content.forEach { appendInlineContent(it, builder, linkColor, codeBackground) }
            }
        }

        is InlineContent.StrongEmphasis -> {
            builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                inline.content.forEach { appendInlineContent(it, builder, linkColor, codeBackground) }
            }
        }

        is InlineContent.Link -> {
            builder.withStyle(
                SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                inline.content.forEach { appendInlineContent(it, builder, linkColor, codeBackground) }
            }
        }

        is InlineContent.LineBreak -> {
            if (inline.isSoft) {
                builder.append(" ")
            } else {
                builder.append("\n")
            }
        }
    }
}
