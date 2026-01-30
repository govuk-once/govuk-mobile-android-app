package uk.gov.govuk.chat.parser

import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser
import uk.gov.govuk.chat.parser.model.InlineContent
import uk.gov.govuk.chat.parser.model.MarkdownElement
import java.util.UUID

class MarkdownParser {
    private val parser: Parser = Parser.builder().build()

    /**
     * Parse markdown string into a list of renderable elements.
     */
    fun parse(markdown: String): List<MarkdownElement> {
        val document = parser.parse(markdown)
        return extractElements(document)
    }

    private fun extractElements(document: Node): List<MarkdownElement> {
        val elements = mutableListOf<MarkdownElement>()
        var child = document.firstChild
        while (child != null) {
            extractElement(child, elements, listDepth = 0)
            child = child.next
        }
        return elements
    }

    private fun extractElement(
        node: Node,
        elements: MutableList<MarkdownElement>,
        listDepth: Int
    ) {
        when (node) {
            is Heading -> {
                val content = extractInlineContent(node)
                val plainText = extractPlainText(node)
                elements.add(
                    MarkdownElement.Heading(
                        id = generateId(),
                        level = node.level,
                        content = content,
                        plainText = plainText
                    )
                )
            }

            is Paragraph -> {
                val content = extractInlineContent(node)
                val plainText = extractPlainText(node)
                elements.add(
                    MarkdownElement.Paragraph(
                        id = generateId(),
                        content = content,
                        plainText = plainText
                    )
                )
            }

            is FencedCodeBlock -> {
                elements.add(
                    MarkdownElement.CodeBlock(
                        id = generateId(),
                        code = node.literal.trimEnd(),
                        language = node.info.takeIf { it.isNotBlank() },
                        plainText = "Code: ${node.literal.trimEnd()}"
                    )
                )
            }

            is IndentedCodeBlock -> {
                elements.add(
                    MarkdownElement.CodeBlock(
                        id = generateId(),
                        code = node.literal.trimEnd(),
                        language = null,
                        plainText = "Code: ${node.literal.trimEnd()}"
                    )
                )
            }

            is BlockQuote -> {
                var child = node.firstChild
                while (child != null) {
                    if (child is Paragraph) {
                        val content = extractInlineContent(child)
                        val plainText = "Quote: ${extractPlainText(child)}"
                        elements.add(
                            MarkdownElement.BlockQuote(
                                id = generateId(),
                                content = content,
                                plainText = plainText
                            )
                        )
                    }
                    child = child.next
                }
            }

            is BulletList -> {
                var number = 1
                var child = node.firstChild
                while (child != null) {
                    if (child is ListItem) {
                        extractListItem(child, elements, listDepth, isOrdered = false, number)
                        number++
                    }
                    child = child.next
                }
            }

            is OrderedList -> {
                var number = node.markerStartNumber ?: 1
                var child = node.firstChild
                while (child != null) {
                    if (child is ListItem) {
                        extractListItem(child, elements, listDepth, isOrdered = true, number)
                        number++
                    }
                    child = child.next
                }
            }

            is ThematicBreak -> {
                elements.add(MarkdownElement.ThematicBreak(id = generateId()))
            }
        }
    }

    private fun extractListItem(
        item: ListItem,
        elements: MutableList<MarkdownElement>,
        depth: Int,
        isOrdered: Boolean,
        number: Int
    ) {
        var child = item.firstChild
        while (child != null) {
            when (child) {
                is Paragraph -> {
                    val content = extractInlineContent(child)
                    val plainText = extractPlainText(child)
                    val prefix = if (isOrdered) "$number." else "\u2022"
                    elements.add(
                        MarkdownElement.ListItem(
                            id = generateId(),
                            content = content,
                            depth = depth,
                            isOrdered = isOrdered,
                            number = if (isOrdered) number else null,
                            plainText = "$prefix $plainText"
                        )
                    )
                }

                is BulletList -> {
                    var nestedNumber = 1
                    var nestedChild = child.firstChild
                    while (nestedChild != null) {
                        if (nestedChild is ListItem) {
                            extractListItem(
                                nestedChild,
                                elements,
                                depth + 1,
                                isOrdered = false,
                                nestedNumber
                            )
                            nestedNumber++
                        }
                        nestedChild = nestedChild.next
                    }
                }

                is OrderedList -> {
                    var nestedNumber = child.markerStartNumber ?: 1
                    var nestedChild = child.firstChild
                    while (nestedChild != null) {
                        if (nestedChild is ListItem) {
                            extractListItem(
                                nestedChild,
                                elements,
                                depth + 1,
                                isOrdered = true,
                                nestedNumber
                            )
                            nestedNumber++
                        }
                        nestedChild = nestedChild.next
                    }
                }
            }
            child = child.next
        }
    }

    private fun extractInlineContent(block: Node): List<InlineContent> {
        val content = mutableListOf<InlineContent>()
        var child = block.firstChild
        while (child != null) {
            content.addAll(nodeToInlineContent(child))
            child = child.next
        }
        return content
    }

    private fun nodeToInlineContent(node: Node): List<InlineContent> {
        return when (node) {
            is Text -> listOf(InlineContent.Text(node.literal))
            is Code -> listOf(InlineContent.Code(node.literal))
            is Emphasis -> listOf(InlineContent.Emphasis(extractChildInlineContent(node)))
            is StrongEmphasis -> listOf(InlineContent.StrongEmphasis(extractChildInlineContent(node)))
            is Link -> listOf(
                InlineContent.Link(
                    content = extractChildInlineContent(node),
                    url = node.destination,
                    title = node.title
                )
            )
            is SoftLineBreak -> listOf(InlineContent.LineBreak(isSoft = true))
            is HardLineBreak -> listOf(InlineContent.LineBreak(isSoft = false))
            else -> emptyList()
        }
    }

    private fun extractChildInlineContent(node: Node): List<InlineContent> {
        val content = mutableListOf<InlineContent>()
        var child = node.firstChild
        while (child != null) {
            content.addAll(nodeToInlineContent(child))
            child = child.next
        }
        return content
    }

    private fun extractPlainText(block: Node): String {
        val builder = StringBuilder()
        extractPlainTextRecursive(block, builder)
        return builder.toString()
    }

    private fun extractPlainTextRecursive(node: Node, builder: StringBuilder) {
        when (node) {
            is Text -> builder.append(node.literal)
            is Code -> builder.append(node.literal)
            is SoftLineBreak -> builder.append(" ")
            is HardLineBreak -> builder.append("\n")
            else -> {
                var child = node.firstChild
                while (child != null) {
                    extractPlainTextRecursive(child, builder)
                    child = child.next
                }
            }
        }
    }

    private fun generateId(): String = UUID.randomUUID().toString()
}
