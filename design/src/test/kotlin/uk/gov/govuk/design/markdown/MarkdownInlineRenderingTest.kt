package uk.gov.govuk.design.markdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import uk.gov.govuk.design.markdown.model.InlineContent

@RunWith(Enclosed::class)
class MarkdownInlineRenderingTest {

    class HasLinksTest {
        @Test
        fun `returns false for empty content`() {
            assertFalse(hasLinks(emptyList()))
        }

        @Test
        fun `returns false when no links present`() {
            val content = listOf(InlineContent.Text("plain text"))
            assertFalse(hasLinks(content))
        }

        @Test
        fun `returns true for a top-level link`() {
            val content = listOf(
                InlineContent.Link(listOf(InlineContent.Text("GOV.UK")), "https://www.gov.uk", null)
            )
            assertTrue(hasLinks(content))
        }

        @Test
        fun `returns true when link is nested inside emphasis`() {
            val content = listOf(
                InlineContent.Emphasis(
                    listOf(InlineContent.Link(listOf(InlineContent.Text("link")), "https://example.com", null))
                )
            )
            assertTrue(hasLinks(content))
        }

        @Test
        fun `returns true when link is nested inside strong emphasis`() {
            val content = listOf(
                InlineContent.StrongEmphasis(
                    listOf(InlineContent.Link(listOf(InlineContent.Text("link")), "https://example.com", null))
                )
            )
            assertTrue(hasLinks(content))
        }

        @Test
        fun `returns false when other inline types have no links`() {
            val content = listOf(
                InlineContent.Code("code"),
                InlineContent.LineBreak(isSoft = true)
            )
            assertFalse(hasLinks(content))
        }
    }

    class CollectLinksTest {
        @Test
        fun `returns empty list when no links`() {
            val content = listOf(InlineContent.Text("plain text"))
            assertTrue(collectLinks(content).isEmpty())
        }

        @Test
        fun `collects a single top-level link`() {
            val content = listOf(
                InlineContent.Link(listOf(InlineContent.Text("GOV.UK")), "https://www.gov.uk", null)
            )
            assertEquals(listOf("https://www.gov.uk"), collectLinks(content))
        }

        @Test
        fun `collects multiple links in order`() {
            val content = listOf(
                InlineContent.Link(listOf(InlineContent.Text("first")), "https://first.com", null),
                InlineContent.Text(" and "),
                InlineContent.Link(listOf(InlineContent.Text("second")), "https://second.com", null)
            )
            assertEquals(listOf("https://first.com", "https://second.com"), collectLinks(content))
        }

        @Test
        fun `collects links nested inside emphasis and strong emphasis`() {
            val content = listOf(
                InlineContent.Emphasis(
                    listOf(InlineContent.Link(listOf(InlineContent.Text("italic link")), "https://italic.com", null))
                ),
                InlineContent.StrongEmphasis(
                    listOf(InlineContent.Link(listOf(InlineContent.Text("bold link")), "https://bold.com", null))
                )
            )
            assertEquals(listOf("https://italic.com", "https://bold.com"), collectLinks(content))
        }
    }

    class AppendInlineContentTest {
        @Test
        fun `appends plain text`() {
            val content = listOf(InlineContent.Text("Hello world"))
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), null, Color.Gray)
            }
            assertEquals("Hello world", result.text)
        }

        @Test
        fun `applies bold span style for strong emphasis`() {
            val content = listOf(InlineContent.StrongEmphasis(listOf(InlineContent.Text("bold"))))
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), null, Color.Gray)
            }
            assertEquals("bold", result.text)
            val boldSpan = result.spanStyles.single { it.item.fontWeight == FontWeight.Bold }
            assertEquals(0, boldSpan.start)
            assertEquals(4, boldSpan.end)
        }

        @Test
        fun `applies italic span style for emphasis`() {
            val content = listOf(InlineContent.Emphasis(listOf(InlineContent.Text("italic"))))
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), null, Color.Gray)
            }
            assertEquals("italic", result.text)
            val italicSpan = result.spanStyles.single { it.item.fontStyle == FontStyle.Italic }
            assertEquals(0, italicSpan.start)
            assertEquals(6, italicSpan.end)
        }

        @Test
        fun `applies monospace font and background for code`() {
            val codeBackground = Color.Gray
            val content = listOf(InlineContent.Code("val x = 1"))
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), null, codeBackground)
            }
            assertEquals("val x = 1", result.text)
            val codeSpan = result.spanStyles.single { it.item.fontFamily == FontFamily.Monospace }
            assertEquals(codeBackground, codeSpan.item.background)
        }

        @Test
        fun `applies link colour and underline when colour is provided`() {
            val linkColor = Color.Blue
            val content = listOf(
                InlineContent.Link(listOf(InlineContent.Text("GOV.UK")), "https://www.gov.uk", null)
            )
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), linkColor, Color.Gray)
            }
            assertEquals("GOV.UK", result.text)
            val linkSpan = result.spanStyles.single { it.item.textDecoration == TextDecoration.Underline }
            assertEquals(linkColor, linkSpan.item.color)
        }

        @Test
        fun `renders link content without colour or underline when colour is null`() {
            val content = listOf(
                InlineContent.Link(listOf(InlineContent.Text("plain")), "https://example.com", null)
            )
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), null, Color.Gray)
            }
            assertEquals("plain", result.text)
            assertTrue(result.spanStyles.none { it.item.textDecoration == TextDecoration.Underline })
        }

        @Test
        fun `appends soft line break as a space`() {
            val content = listOf(
                InlineContent.Text("a"),
                InlineContent.LineBreak(isSoft = true),
                InlineContent.Text("b")
            )
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), null, Color.Gray)
            }
            assertEquals("a b", result.text)
        }

        @Test
        fun `appends hard line break as a newline`() {
            val content = listOf(
                InlineContent.Text("a"),
                InlineContent.LineBreak(isSoft = false),
                InlineContent.Text("b")
            )
            val result = buildAnnotatedString {
                appendInlineContent(content, this, emptyList(), null, Color.Gray)
            }
            assertEquals("a\nb", result.text)
        }

        @Test
        fun `applies inherited styles alongside its own style`() {
            val inheritedStyles = listOf(SpanStyle(fontWeight = FontWeight.Bold))
            val content = listOf(InlineContent.Text("text"))
            val result = buildAnnotatedString {
                appendInlineContent(content, this, inheritedStyles, null, Color.Gray)
            }
            assertEquals("text", result.text)
            assertTrue(result.spanStyles.any { it.item.fontWeight == FontWeight.Bold })
        }
    }

    class SplitIntoSegmentsTest {
        @Test
        fun `returns a single empty segment for empty content`() {
            val segments = splitIntoSegments(emptyList(), Color.Blue, Color.Gray)
            assertEquals(1, segments.size)
            assertEquals("", segments[0].text.text)
            assertEquals(null, segments[0].linkUrl)
        }

        @Test
        fun `returns a single text segment when no links present`() {
            val content = listOf(InlineContent.Text("Hello world"))
            val segments = splitIntoSegments(content, Color.Blue, Color.Gray)
            assertEquals(1, segments.size)
            assertEquals("Hello world", segments[0].text.text)
            assertEquals(null, segments[0].linkUrl)
        }

        @Test
        fun `splits text either side of a link into separate segments`() {
            val content = listOf(
                InlineContent.Text("Visit "),
                InlineContent.Link(listOf(InlineContent.Text("GOV.UK")), "https://www.gov.uk", null),
                InlineContent.Text(" now")
            )
            val segments = splitIntoSegments(content, Color.Blue, Color.Gray)

            assertEquals(3, segments.size)
            assertEquals("Visit ", segments[0].text.text)
            assertEquals(null, segments[0].linkUrl)

            assertEquals("GOV.UK", segments[1].text.text)
            assertEquals("https://www.gov.uk", segments[1].linkUrl)

            assertEquals(" now", segments[2].text.text)
            assertEquals(null, segments[2].linkUrl)
        }

        @Test
        fun `splits multiple links into their own segments`() {
            val content = listOf(
                InlineContent.Link(listOf(InlineContent.Text("first")), "https://first.com", null),
                InlineContent.Text(" and "),
                InlineContent.Link(listOf(InlineContent.Text("second")), "https://second.com", null)
            )
            val segments = splitIntoSegments(content, Color.Blue, Color.Gray)

            assertEquals(3, segments.size)
            assertEquals("https://first.com", segments[0].linkUrl)
            assertEquals(" and ", segments[1].text.text)
            assertEquals("https://second.com", segments[2].linkUrl)
        }

        @Test
        fun `preserves emphasis styling on non-link segments`() {
            val content = listOf(
                InlineContent.StrongEmphasis(listOf(InlineContent.Text("bold"))),
                InlineContent.Link(listOf(InlineContent.Text("link")), "https://example.com", null)
            )
            val segments = splitIntoSegments(content, Color.Blue, Color.Gray)

            assertEquals(2, segments.size)
            val boldSegment = segments[0]
            assertEquals("bold", boldSegment.text.text)
            assertTrue(boldSegment.text.spanStyles.any { it.item.fontWeight == FontWeight.Bold })
        }

        @Test
        fun `link segment text uses the given link colour`() {
            val linkColor = Color.Blue
            val content = listOf(
                InlineContent.Link(listOf(InlineContent.Text("GOV.UK")), "https://www.gov.uk", null)
            )
            val segments = splitIntoSegments(content, linkColor, Color.Gray)

            assertEquals(1, segments.size)
            val linkSpan = segments[0].text.spanStyles.single { it.item.textDecoration == TextDecoration.Underline }
            assertEquals(linkColor, linkSpan.item.color)
        }
    }
}
