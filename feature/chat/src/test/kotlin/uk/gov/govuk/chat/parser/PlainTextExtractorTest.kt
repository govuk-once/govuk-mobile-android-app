package uk.gov.govuk.chat.parser

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import uk.gov.govuk.chat.parser.model.InlineContent
import uk.gov.govuk.chat.parser.model.MarkdownElement

@RunWith(Enclosed::class)
class PlainTextExtractorTest {

    class ExtractAllTest {
        @Test
        fun `extracts text from single paragraph`() {
            val elements = listOf(
                MarkdownElement.Paragraph(
                    id = "1",
                    content = listOf(InlineContent.Text("Hello world")),
                    plainText = "Hello world"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("Hello world", result)
        }

        @Test
        fun `separates multiple elements with double newlines`() {
            val elements = listOf(
                MarkdownElement.Paragraph(
                    id = "1",
                    content = listOf(InlineContent.Text("First")),
                    plainText = "First"
                ),
                MarkdownElement.Paragraph(
                    id = "2",
                    content = listOf(InlineContent.Text("Second")),
                    plainText = "Second"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("First\n\nSecond", result)
        }

        @Test
        fun `extracts text from heading`() {
            val elements = listOf(
                MarkdownElement.Heading(
                    id = "1",
                    level = 1,
                    content = listOf(InlineContent.Text("Title")),
                    plainText = "Title"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("Title", result)
        }

        @Test
        fun `extracts text from code block`() {
            val elements = listOf(
                MarkdownElement.CodeBlock(
                    id = "1",
                    code = "println(\"hello\")",
                    language = "kotlin",
                    plainText = "println(\"hello\")"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("println(\"hello\")", result)
        }

        @Test
        fun `extracts text from block quote`() {
            val elements = listOf(
                MarkdownElement.BlockQuote(
                    id = "1",
                    content = listOf(InlineContent.Text("Important quote")),
                    plainText = "Important quote"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("Important quote", result)
        }

        @Test
        fun `extracts text from unordered list item`() {
            val elements = listOf(
                MarkdownElement.ListItem(
                    id = "1",
                    content = listOf(InlineContent.Text("List item")),
                    depth = 0,
                    isOrdered = false,
                    number = null,
                    plainText = "\u2022 List item"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("\u2022 List item", result)
        }

        @Test
        fun `extracts text from ordered list item`() {
            val elements = listOf(
                MarkdownElement.ListItem(
                    id = "1",
                    content = listOf(InlineContent.Text("First item")),
                    depth = 0,
                    isOrdered = true,
                    number = 1,
                    plainText = "1. First item"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("1. First item", result)
        }

        @Test
        fun `extracts text from thematic break`() {
            val elements = listOf(
                MarkdownElement.ThematicBreak(id = "1")
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("---", result)
        }

        @Test
        fun `extracts text from empty list`() {
            val elements = emptyList<MarkdownElement>()

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("", result)
        }

        @Test
        fun `extracts text from mixed content`() {
            val elements = listOf(
                MarkdownElement.Heading(
                    id = "1",
                    level = 1,
                    content = listOf(InlineContent.Text("Welcome")),
                    plainText = "Welcome"
                ),
                MarkdownElement.Paragraph(
                    id = "2",
                    content = listOf(InlineContent.Text("Hello")),
                    plainText = "Hello"
                ),
                MarkdownElement.ListItem(
                    id = "3",
                    content = listOf(InlineContent.Text("Item")),
                    depth = 0,
                    isOrdered = false,
                    number = null,
                    plainText = "\u2022 Item"
                )
            )

            val result = PlainTextExtractor.extractAll(elements)

            assertEquals("Welcome\n\nHello\n\n\u2022 Item", result)
        }
    }

    class InlineToPlainTextTest {
        @Test
        fun `extracts plain text`() {
            val content = listOf(InlineContent.Text("Hello world"))

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("Hello world", result)
        }

        @Test
        fun `extracts text from inline code`() {
            val content = listOf(InlineContent.Code("println()"))

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("println()", result)
        }

        @Test
        fun `extracts text from emphasis`() {
            val content = listOf(
                InlineContent.Emphasis(listOf(InlineContent.Text("italic")))
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("italic", result)
        }

        @Test
        fun `extracts text from strong emphasis`() {
            val content = listOf(
                InlineContent.StrongEmphasis(listOf(InlineContent.Text("bold")))
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("bold", result)
        }

        @Test
        fun `extracts text from link with url in parentheses`() {
            val content = listOf(
                InlineContent.Link(
                    content = listOf(InlineContent.Text("GOV.UK")),
                    url = "https://www.gov.uk",
                    title = null
                )
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("GOV.UK (https://www.gov.uk)", result)
        }

        @Test
        fun `extracts text from soft line break as space`() {
            val content = listOf(
                InlineContent.Text("Line one"),
                InlineContent.LineBreak(isSoft = true),
                InlineContent.Text("Line two")
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("Line one Line two", result)
        }

        @Test
        fun `extracts text from hard line break as newline`() {
            val content = listOf(
                InlineContent.Text("Line one"),
                InlineContent.LineBreak(isSoft = false),
                InlineContent.Text("Line two")
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("Line one\nLine two", result)
        }

        @Test
        fun `extracts text from mixed inline content`() {
            val content = listOf(
                InlineContent.Text("Visit "),
                InlineContent.Link(
                    content = listOf(InlineContent.Text("GOV.UK")),
                    url = "https://www.gov.uk",
                    title = null
                ),
                InlineContent.Text(" for "),
                InlineContent.StrongEmphasis(listOf(InlineContent.Text("important"))),
                InlineContent.Text(" info.")
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("Visit GOV.UK (https://www.gov.uk) for important info.", result)
        }

        @Test
        fun `extracts text from nested emphasis`() {
            val content = listOf(
                InlineContent.StrongEmphasis(
                    listOf(
                        InlineContent.Emphasis(
                            listOf(InlineContent.Text("bold and italic"))
                        )
                    )
                )
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("bold and italic", result)
        }

        @Test
        fun `extracts text from link with formatted content`() {
            val content = listOf(
                InlineContent.Link(
                    content = listOf(
                        InlineContent.StrongEmphasis(listOf(InlineContent.Text("Bold link")))
                    ),
                    url = "https://example.com",
                    title = null
                )
            )

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("Bold link (https://example.com)", result)
        }

        @Test
        fun `extracts text from empty content list`() {
            val content = emptyList<InlineContent>()

            val result = PlainTextExtractor.inlineToPlainText(content)

            assertEquals("", result)
        }
    }
}
