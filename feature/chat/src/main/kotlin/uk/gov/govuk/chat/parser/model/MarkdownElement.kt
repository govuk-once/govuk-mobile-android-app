package uk.gov.govuk.chat.parser.model

/**
 * Represents a single renderable markdown element.
 * Each element corresponds to one focusable unit in the UI.
 */
sealed class MarkdownElement {
    /** Unique identifier for accessibility and state management */
    abstract val id: String

    /** Plain text representation for copy/paste and screen readers */
    abstract val plainText: String

    data class Heading(
        override val id: String,
        val level: Int,
        val content: List<InlineContent>,
        override val plainText: String
    ) : MarkdownElement()

    data class Paragraph(
        override val id: String,
        val content: List<InlineContent>,
        override val plainText: String
    ) : MarkdownElement()

    data class CodeBlock(
        override val id: String,
        val code: String,
        val language: String?,
        override val plainText: String
    ) : MarkdownElement()

    data class BlockQuote(
        override val id: String,
        val content: List<InlineContent>,
        override val plainText: String
    ) : MarkdownElement()

    data class ListItem(
        override val id: String,
        val content: List<InlineContent>,
        val depth: Int,
        val isOrdered: Boolean,
        val number: Int?,
        override val plainText: String
    ) : MarkdownElement()

    data class ThematicBreak(
        override val id: String,
        override val plainText: String = "---"
    ) : MarkdownElement()
}

/**
 * Inline content within a block element (supports styling).
 */
sealed class InlineContent {
    data class Text(val text: String) : InlineContent()
    data class Emphasis(val content: List<InlineContent>) : InlineContent()
    data class StrongEmphasis(val content: List<InlineContent>) : InlineContent()
    data class Code(val code: String) : InlineContent()
    data class Link(
        val content: List<InlineContent>,
        val url: String,
        val title: String?
    ) : InlineContent()
    data class LineBreak(val isSoft: Boolean) : InlineContent()
}
