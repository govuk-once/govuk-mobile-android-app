package uk.gov.govuk.design.markdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import uk.gov.govuk.design.markdown.model.InlineContent

data class ContentSegment(
    val text: AnnotatedString,
    val linkUrl: String? = null
)

fun hasLinks(content: List<InlineContent>): Boolean {
    return content.any { inline ->
        when (inline) {
            is InlineContent.Link -> true
            is InlineContent.Emphasis -> hasLinks(inline.content)
            is InlineContent.StrongEmphasis -> hasLinks(inline.content)
            else -> false
        }
    }
}

fun collectLinks(content: List<InlineContent>): List<String> {
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
                else -> Unit
            }
        }
    }
    collect(content)
    return links
}

fun appendInlineContent(
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

fun splitIntoSegments(
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
