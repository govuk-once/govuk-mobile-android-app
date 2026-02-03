package uk.gov.govuk.chat.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import uk.gov.govuk.chat.parser.model.InlineContent
import uk.gov.govuk.chat.parser.model.MarkdownElement

@RunWith(Enclosed::class)
class MarkdownParserTest {

    class ParseHeadingsTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses h1 heading`() {
            val result = parser.parse("# Hello World")

            assertEquals(1, result.size)
            val heading = result[0] as MarkdownElement.Heading
            assertEquals(1, heading.level)
            assertEquals("Hello World", heading.plainText)
        }

        @Test
        fun `parses h2 heading`() {
            val result = parser.parse("## Section Title")

            assertEquals(1, result.size)
            val heading = result[0] as MarkdownElement.Heading
            assertEquals(2, heading.level)
            assertEquals("Section Title", heading.plainText)
        }

        @Test
        fun `parses h6 heading`() {
            val result = parser.parse("###### Deep Heading")

            assertEquals(1, result.size)
            val heading = result[0] as MarkdownElement.Heading
            assertEquals(6, heading.level)
            assertEquals("Deep Heading", heading.plainText)
        }

        @Test
        fun `parses heading with inline formatting`() {
            val result = parser.parse("# Hello **bold** and *italic*")

            assertEquals(1, result.size)
            val heading = result[0] as MarkdownElement.Heading
            assertEquals(1, heading.level)
            assertEquals("Hello bold and italic", heading.plainText)
            assertEquals(5, heading.content.size)
        }
    }

    class ParseParagraphsTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses simple paragraph`() {
            val result = parser.parse("This is a paragraph.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("This is a paragraph.", paragraph.plainText)
        }

        @Test
        fun `parses multiple paragraphs`() {
            val result = parser.parse("First paragraph.\n\nSecond paragraph.")

            assertEquals(2, result.size)
            assertTrue(result[0] is MarkdownElement.Paragraph)
            assertTrue(result[1] is MarkdownElement.Paragraph)
            assertEquals("First paragraph.", (result[0] as MarkdownElement.Paragraph).plainText)
            assertEquals("Second paragraph.", (result[1] as MarkdownElement.Paragraph).plainText)
        }

        @Test
        fun `parses paragraph with bold text`() {
            val result = parser.parse("This has **bold** text.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("This has bold text.", paragraph.plainText)
            assertEquals(3, paragraph.content.size)
            assertTrue(paragraph.content[1] is InlineContent.StrongEmphasis)
        }

        @Test
        fun `parses paragraph with italic text`() {
            val result = parser.parse("This has *italic* text.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("This has italic text.", paragraph.plainText)
            assertTrue(paragraph.content[1] is InlineContent.Emphasis)
        }

        @Test
        fun `parses paragraph with inline code`() {
            val result = parser.parse("Use the `println()` function.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("Use the println() function.", paragraph.plainText)
            assertTrue(paragraph.content[1] is InlineContent.Code)
            assertEquals("println()", (paragraph.content[1] as InlineContent.Code).code)
        }

        @Test
        fun `parses paragraph with link`() {
            val result = parser.parse("Visit [GOV.UK](https://www.gov.uk) for more info.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals(3, paragraph.content.size)
            val link = paragraph.content[1] as InlineContent.Link
            assertEquals("https://www.gov.uk", link.url)
            assertEquals(1, link.content.size)
            assertEquals("GOV.UK", (link.content[0] as InlineContent.Text).text)
        }

        @Test
        fun `parses paragraph with soft line break`() {
            val result = parser.parse("Line one\nLine two")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("Line one Line two", paragraph.plainText)
        }

        @Test
        fun `parses paragraph with hard line break`() {
            val result = parser.parse("Line one  \nLine two")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("Line one\nLine two", paragraph.plainText)
        }
    }

    class ParseCodeBlocksTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses fenced code block without language`() {
            val result = parser.parse("```\nval x = 1\n```")

            assertEquals(1, result.size)
            val codeBlock = result[0] as MarkdownElement.CodeBlock
            assertEquals("val x = 1", codeBlock.code)
            assertNull(codeBlock.language)
            assertEquals("Code: val x = 1", codeBlock.plainText)
        }

        @Test
        fun `parses fenced code block with language`() {
            val result = parser.parse("```kotlin\nfun hello() = println(\"Hello\")\n```")

            assertEquals(1, result.size)
            val codeBlock = result[0] as MarkdownElement.CodeBlock
            assertEquals("fun hello() = println(\"Hello\")", codeBlock.code)
            assertEquals("kotlin", codeBlock.language)
        }

        @Test
        fun `parses indented code block`() {
            val result = parser.parse("    val x = 1\n    val y = 2")

            assertEquals(1, result.size)
            val codeBlock = result[0] as MarkdownElement.CodeBlock
            assertTrue(codeBlock.code.contains("val x = 1"))
            assertTrue(codeBlock.code.contains("val y = 2"))
            assertNull(codeBlock.language)
        }

        @Test
        fun `trims trailing whitespace from code`() {
            val result = parser.parse("```\ncode here   \n```")

            val codeBlock = result[0] as MarkdownElement.CodeBlock
            assertEquals("code here", codeBlock.code)
        }
    }

    class ParseBlockQuotesTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses simple block quote`() {
            val result = parser.parse("> This is a quote")

            assertEquals(1, result.size)
            val blockQuote = result[0] as MarkdownElement.BlockQuote
            assertEquals("Quote: This is a quote", blockQuote.plainText)
        }

        @Test
        fun `parses block quote with inline formatting`() {
            val result = parser.parse("> This is **important**")

            assertEquals(1, result.size)
            val blockQuote = result[0] as MarkdownElement.BlockQuote
            assertEquals("Quote: This is important", blockQuote.plainText)
            assertEquals(2, blockQuote.content.size)
            assertTrue(blockQuote.content[1] is InlineContent.StrongEmphasis)
        }

        @Test
        fun `parses multi-paragraph block quote`() {
            val result = parser.parse("> First paragraph\n>\n> Second paragraph")

            assertEquals(2, result.size)
            assertTrue(result[0] is MarkdownElement.BlockQuote)
            assertTrue(result[1] is MarkdownElement.BlockQuote)
        }
    }

    class ParseBulletListsTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses simple bullet list`() {
            val result = parser.parse("- Item 1\n- Item 2\n- Item 3")

            assertEquals(3, result.size)
            result.forEachIndexed { index, element ->
                val listItem = element as MarkdownElement.ListItem
                assertEquals(false, listItem.isOrdered)
                assertNull(listItem.number)
                assertEquals(0, listItem.depth)
                assertEquals("\u2022 Item ${index + 1}", listItem.plainText)
            }
        }

        @Test
        fun `parses bullet list with asterisks`() {
            val result = parser.parse("* Item 1\n* Item 2")

            assertEquals(2, result.size)
            result.forEach { element ->
                assertTrue(element is MarkdownElement.ListItem)
                assertEquals(false, (element as MarkdownElement.ListItem).isOrdered)
            }
        }

        @Test
        fun `parses nested bullet list`() {
            val result = parser.parse("- Parent\n  - Child\n  - Child 2")

            assertEquals(3, result.size)
            val parent = result[0] as MarkdownElement.ListItem
            val child1 = result[1] as MarkdownElement.ListItem
            val child2 = result[2] as MarkdownElement.ListItem

            assertEquals(0, parent.depth)
            assertEquals(1, child1.depth)
            assertEquals(1, child2.depth)
        }

        @Test
        fun `parses bullet list item with formatting`() {
            val result = parser.parse("- This is **bold** and *italic*")

            assertEquals(1, result.size)
            val listItem = result[0] as MarkdownElement.ListItem
            assertEquals("\u2022 This is bold and italic", listItem.plainText)
        }
    }

    class ParseOrderedListsTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses simple ordered list`() {
            val result = parser.parse("1. First\n2. Second\n3. Third")

            assertEquals(3, result.size)
            result.forEachIndexed { index, element ->
                val listItem = element as MarkdownElement.ListItem
                assertEquals(true, listItem.isOrdered)
                assertEquals(index + 1, listItem.number)
                assertEquals(0, listItem.depth)
            }
        }

        @Test
        fun `parses ordered list starting from different number`() {
            val result = parser.parse("5. Fifth item\n6. Sixth item")

            assertEquals(2, result.size)
            assertEquals(5, (result[0] as MarkdownElement.ListItem).number)
            assertEquals(6, (result[1] as MarkdownElement.ListItem).number)
        }

        @Test
        fun `parses nested ordered list`() {
            val result = parser.parse("1. Parent\n   1. Child\n   2. Child 2")

            assertEquals(3, result.size)
            val parent = result[0] as MarkdownElement.ListItem
            val child1 = result[1] as MarkdownElement.ListItem
            val child2 = result[2] as MarkdownElement.ListItem

            assertEquals(0, parent.depth)
            assertEquals(true, parent.isOrdered)
            assertEquals(1, child1.depth)
            assertEquals(true, child1.isOrdered)
            assertEquals(1, child2.depth)
        }

        @Test
        fun `parses ordered list with bullet sublist`() {
            val result = parser.parse("1. Parent\n   - Bullet child")

            assertEquals(2, result.size)
            val parent = result[0] as MarkdownElement.ListItem
            val child = result[1] as MarkdownElement.ListItem

            assertEquals(true, parent.isOrdered)
            assertEquals(false, child.isOrdered)
            assertEquals(1, child.depth)
        }
    }

    class ParseThematicBreakTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses thematic break with dashes`() {
            val result = parser.parse("---")

            assertEquals(1, result.size)
            assertTrue(result[0] is MarkdownElement.ThematicBreak)
        }

        @Test
        fun `parses thematic break with asterisks`() {
            val result = parser.parse("***")

            assertEquals(1, result.size)
            assertTrue(result[0] is MarkdownElement.ThematicBreak)
        }

        @Test
        fun `parses thematic break with underscores`() {
            val result = parser.parse("___")

            assertEquals(1, result.size)
            assertTrue(result[0] is MarkdownElement.ThematicBreak)
        }
    }

    class ParseMixedContentTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses document with multiple element types`() {
            val markdown = """
                # Welcome

                This is a paragraph.

                - Item 1
                - Item 2

                > A quote

                ```kotlin
                println("code")
                ```
            """.trimIndent()

            val result = parser.parse(markdown)

            assertTrue(result[0] is MarkdownElement.Heading)
            assertTrue(result[1] is MarkdownElement.Paragraph)
            assertTrue(result[2] is MarkdownElement.ListItem)
            assertTrue(result[3] is MarkdownElement.ListItem)
            assertTrue(result[4] is MarkdownElement.BlockQuote)
            assertTrue(result[5] is MarkdownElement.CodeBlock)
        }

        @Test
        fun `parses empty string`() {
            val result = parser.parse("")

            assertTrue(result.isEmpty())
        }

        @Test
        fun `each element has unique id`() {
            val result = parser.parse("# Heading\n\nParagraph")

            val ids = result.map { it.id }
            assertEquals(ids.size, ids.toSet().size)
        }
    }

    class ParseInlineContentTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses nested emphasis`() {
            val result = parser.parse("This is ***bold and italic***")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("This is bold and italic", paragraph.plainText)
        }

        @Test
        fun `parses link with title`() {
            val result = parser.parse("[Link](https://example.com \"Title\")")

            val paragraph = result[0] as MarkdownElement.Paragraph
            val link = paragraph.content[0] as InlineContent.Link
            assertEquals("https://example.com", link.url)
            assertEquals("Title", link.title)
        }

        @Test
        fun `parses bold text inside link`() {
            val result = parser.parse("[**Bold link**](https://example.com)")

            val paragraph = result[0] as MarkdownElement.Paragraph
            val link = paragraph.content[0] as InlineContent.Link
            assertTrue(link.content[0] is InlineContent.StrongEmphasis)
        }
    }
}
