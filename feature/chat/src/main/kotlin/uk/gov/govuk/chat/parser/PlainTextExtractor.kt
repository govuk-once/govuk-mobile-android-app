package uk.gov.govuk.chat.parser

import uk.gov.govuk.chat.parser.model.InlineContent
import uk.gov.govuk.chat.parser.model.MarkdownElement

/**
 * Extracts all plain text from a list of markdown elements.
 * Useful for copy-to-clipboard functionality.
 */
object PlainTextExtractor {

    /**
     * Convert all elements to a single plain text string.
     * Links are formatted as "text (url)".
     */
    fun extractAll(elements: List<MarkdownElement>): String {
        return elements.joinToString("\n\n") { element ->
            extractElementText(element)
        }
    }

    private fun extractElementText(element: MarkdownElement): String {
        return when (element) {
            is MarkdownElement.Heading -> inlineToPlainText(element.content)
            is MarkdownElement.Paragraph -> inlineToPlainText(element.content)
            is MarkdownElement.CodeBlock -> "Code: ${element.code}"
            is MarkdownElement.BlockQuote -> "Quote: ${inlineToPlainText(element.content)}"
            is MarkdownElement.ListItem -> {
                val prefix = if (element.isOrdered) "${element.number}." else "\u2022"
                "$prefix ${inlineToPlainText(element.content)}"
            }
            is MarkdownElement.ThematicBreak -> "---"
        }
    }

    /**
     * Convert inline content to plain text.
     * Links include their URL in parentheses.
     */
    fun inlineToPlainText(content: List<InlineContent>): String {
        return buildString {
            content.forEach { inline ->
                append(inlineNodeToText(inline))
            }
        }
    }

    private fun inlineNodeToText(inline: InlineContent): String {
        return when (inline) {
            is InlineContent.Text -> inline.text
            is InlineContent.Code -> inline.code
            is InlineContent.Emphasis -> inlineToPlainText(inline.content)
            is InlineContent.StrongEmphasis -> inlineToPlainText(inline.content)
            is InlineContent.Link -> {
                val linkText = inlineToPlainText(inline.content)
                "$linkText (${inline.url})"
            }
            is InlineContent.LineBreak -> if (inline.isSoft) " " else "\n"
        }
    }
}
