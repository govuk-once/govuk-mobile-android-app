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
            assertEquals(4, heading.content.size)

            val text1 = heading.content[0] as InlineContent.Text
            assertEquals("Hello ", text1.text)

            val strong = heading.content[1] as InlineContent.StrongEmphasis
            assertEquals(1, strong.content.size)
            assertEquals("bold", (strong.content[0] as InlineContent.Text).text)

            val text2 = heading.content[2] as InlineContent.Text
            assertEquals(" and ", text2.text)

            val emphasis = heading.content[3] as InlineContent.Emphasis
            assertEquals(1, emphasis.content.size)
            assertEquals("italic", (emphasis.content[0] as InlineContent.Text).text)
        }

        @Test
        fun `parses heading with simple text content`() {
            val result = parser.parse("# Simple heading")

            val heading = result[0] as MarkdownElement.Heading
            assertEquals(1, heading.content.size)
            val text = heading.content[0] as InlineContent.Text
            assertEquals("Simple heading", text.text)
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
            assertEquals(1, paragraph.content.size)

            val text = paragraph.content[0] as InlineContent.Text
            assertEquals("This is a paragraph.", text.text)
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

            val text1 = paragraph.content[0] as InlineContent.Text
            assertEquals("This has ", text1.text)

            val strong = paragraph.content[1] as InlineContent.StrongEmphasis
            assertEquals(1, strong.content.size)
            assertEquals("bold", (strong.content[0] as InlineContent.Text).text)

            val text2 = paragraph.content[2] as InlineContent.Text
            assertEquals(" text.", text2.text)
        }

        @Test
        fun `parses paragraph with italic text`() {
            val result = parser.parse("This has *italic* text.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("This has italic text.", paragraph.plainText)
            assertEquals(3, paragraph.content.size)

            val text1 = paragraph.content[0] as InlineContent.Text
            assertEquals("This has ", text1.text)

            val emphasis = paragraph.content[1] as InlineContent.Emphasis
            assertEquals(1, emphasis.content.size)
            assertEquals("italic", (emphasis.content[0] as InlineContent.Text).text)

            val text2 = paragraph.content[2] as InlineContent.Text
            assertEquals(" text.", text2.text)
        }

        @Test
        fun `parses paragraph with inline code`() {
            val result = parser.parse("Use the `println()` function.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("Use the println() function.", paragraph.plainText)
            assertEquals(3, paragraph.content.size)

            val text1 = paragraph.content[0] as InlineContent.Text
            assertEquals("Use the ", text1.text)

            val code = paragraph.content[1] as InlineContent.Code
            assertEquals("println()", code.code)

            val text2 = paragraph.content[2] as InlineContent.Text
            assertEquals(" function.", text2.text)
        }

        @Test
        fun `parses paragraph with link`() {
            val result = parser.parse("Visit [GOV.UK](https://www.gov.uk) for more info.")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals(3, paragraph.content.size)

            val text1 = paragraph.content[0] as InlineContent.Text
            assertEquals("Visit ", text1.text)

            val link = paragraph.content[1] as InlineContent.Link
            assertEquals("https://www.gov.uk", link.url)
            assertNull(link.title)
            assertEquals(1, link.content.size)
            assertEquals("GOV.UK", (link.content[0] as InlineContent.Text).text)

            val text2 = paragraph.content[2] as InlineContent.Text
            assertEquals(" for more info.", text2.text)
        }

        @Test
        fun `parses paragraph with soft line break`() {
            val result = parser.parse("Line one\nLine two")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("Line one Line two", paragraph.plainText)
            assertEquals(3, paragraph.content.size)

            val text1 = paragraph.content[0] as InlineContent.Text
            assertEquals("Line one", text1.text)

            val lineBreak = paragraph.content[1] as InlineContent.LineBreak
            assertTrue(lineBreak.isSoft)

            val text2 = paragraph.content[2] as InlineContent.Text
            assertEquals("Line two", text2.text)
        }

        @Test
        fun `parses paragraph with hard line break`() {
            val result = parser.parse("Line one  \nLine two")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("Line one\nLine two", paragraph.plainText)
            assertEquals(3, paragraph.content.size)

            val text1 = paragraph.content[0] as InlineContent.Text
            assertEquals("Line one", text1.text)

            val lineBreak = paragraph.content[1] as InlineContent.LineBreak
            assertEquals(false, lineBreak.isSoft)

            val text2 = paragraph.content[2] as InlineContent.Text
            assertEquals("Line two", text2.text)
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
            assertEquals(1, blockQuote.content.size)

            val text = blockQuote.content[0] as InlineContent.Text
            assertEquals("This is a quote", text.text)
        }

        @Test
        fun `parses block quote with inline formatting`() {
            val result = parser.parse("> This is **important**")

            assertEquals(1, result.size)
            val blockQuote = result[0] as MarkdownElement.BlockQuote
            assertEquals("Quote: This is important", blockQuote.plainText)
            assertEquals(2, blockQuote.content.size)

            val text = blockQuote.content[0] as InlineContent.Text
            assertEquals("This is ", text.text)

            val strong = blockQuote.content[1] as InlineContent.StrongEmphasis
            assertEquals(1, strong.content.size)
            assertEquals("important", (strong.content[0] as InlineContent.Text).text)
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

                assertEquals(1, listItem.content.size)
                val text = listItem.content[0] as InlineContent.Text
                assertEquals("Item ${index + 1}", text.text)
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
            assertEquals(4, listItem.content.size)

            val text1 = listItem.content[0] as InlineContent.Text
            assertEquals("This is ", text1.text)

            val strong = listItem.content[1] as InlineContent.StrongEmphasis
            assertEquals(1, strong.content.size)
            assertEquals("bold", (strong.content[0] as InlineContent.Text).text)

            val text2 = listItem.content[2] as InlineContent.Text
            assertEquals(" and ", text2.text)

            val emphasis = listItem.content[3] as InlineContent.Emphasis
            assertEquals(1, emphasis.content.size)
            assertEquals("italic", (emphasis.content[0] as InlineContent.Text).text)
        }

        @Test
        fun `parses bullet list item with link`() {
            val result = parser.parse("- Visit [GOV.UK](https://www.gov.uk)")

            assertEquals(1, result.size)
            val listItem = result[0] as MarkdownElement.ListItem
            assertEquals(2, listItem.content.size)

            val text = listItem.content[0] as InlineContent.Text
            assertEquals("Visit ", text.text)

            val link = listItem.content[1] as InlineContent.Link
            assertEquals("https://www.gov.uk", link.url)
            assertEquals("GOV.UK", (link.content[0] as InlineContent.Text).text)
        }
    }

    class ParseOrderedListsTest {
        private val parser = MarkdownParser()

        @Test
        fun `parses simple ordered list`() {
            val result = parser.parse("1. First\n2. Second\n3. Third")
            val expectedTexts = listOf("First", "Second", "Third")

            assertEquals(3, result.size)
            result.forEachIndexed { index, element ->
                val listItem = element as MarkdownElement.ListItem
                assertEquals(true, listItem.isOrdered)
                assertEquals(index + 1, listItem.number)
                assertEquals(0, listItem.depth)

                assertEquals(1, listItem.content.size)
                val text = listItem.content[0] as InlineContent.Text
                assertEquals(expectedTexts[index], text.text)
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
        fun `parses nested emphasis - bold containing italic`() {
            val result = parser.parse("This is ***bold and italic***")

            assertEquals(1, result.size)
            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals("This is bold and italic", paragraph.plainText)
            assertEquals(2, paragraph.content.size)

            val text = paragraph.content[0] as InlineContent.Text
            assertEquals("This is ", text.text)

            val emphasis = paragraph.content[1] as InlineContent.Emphasis
            assertEquals(1, emphasis.content.size)

            val strong = emphasis.content[0] as InlineContent.StrongEmphasis
            assertEquals(1, strong.content.size)
            assertEquals("bold and italic", (strong.content[0] as InlineContent.Text).text)
        }

        @Test
        fun `parses link with title`() {
            val result = parser.parse("[Link](https://example.com \"Title\")")

            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals(1, paragraph.content.size)

            val link = paragraph.content[0] as InlineContent.Link
            assertEquals("https://example.com", link.url)
            assertEquals("Title", link.title)
            assertEquals(1, link.content.size)
            assertEquals("Link", (link.content[0] as InlineContent.Text).text)
        }

        @Test
        fun `parses bold text inside link`() {
            val result = parser.parse("[**Bold link**](https://example.com)")

            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals(1, paragraph.content.size)

            val link = paragraph.content[0] as InlineContent.Link
            assertEquals("https://example.com", link.url)
            assertEquals(1, link.content.size)

            val strong = link.content[0] as InlineContent.StrongEmphasis
            assertEquals(1, strong.content.size)
            assertEquals("Bold link", (strong.content[0] as InlineContent.Text).text)
        }

        @Test
        fun `parses italic text inside link`() {
            val result = parser.parse("[*Italic link*](https://example.com)")

            val paragraph = result[0] as MarkdownElement.Paragraph
            val link = paragraph.content[0] as InlineContent.Link
            assertEquals(1, link.content.size)

            val emphasis = link.content[0] as InlineContent.Emphasis
            assertEquals(1, emphasis.content.size)
            assertEquals("Italic link", (emphasis.content[0] as InlineContent.Text).text)
        }

        @Test
        fun `parses multiple inline elements in sequence`() {
            val result = parser.parse("**bold** then *italic* then `code`")

            val paragraph = result[0] as MarkdownElement.Paragraph
            assertEquals(5, paragraph.content.size)

            val strong = paragraph.content[0] as InlineContent.StrongEmphasis
            assertEquals("bold", (strong.content[0] as InlineContent.Text).text)

            val text1 = paragraph.content[1] as InlineContent.Text
            assertEquals(" then ", text1.text)

            val emphasis = paragraph.content[2] as InlineContent.Emphasis
            assertEquals("italic", (emphasis.content[0] as InlineContent.Text).text)

            val text2 = paragraph.content[3] as InlineContent.Text
            assertEquals(" then ", text2.text)

            val code = paragraph.content[4] as InlineContent.Code
            assertEquals("code", code.code)
        }

        @Test
        fun `parses link with multiple content elements`() {
            val result = parser.parse("[Visit **GOV.UK** now](https://www.gov.uk)")

            val paragraph = result[0] as MarkdownElement.Paragraph
            val link = paragraph.content[0] as InlineContent.Link
            assertEquals(3, link.content.size)

            val text1 = link.content[0] as InlineContent.Text
            assertEquals("Visit ", text1.text)

            val strong = link.content[1] as InlineContent.StrongEmphasis
            assertEquals("GOV.UK", (strong.content[0] as InlineContent.Text).text)

            val text2 = link.content[2] as InlineContent.Text
            assertEquals(" now", text2.text)
        }
    }
}
