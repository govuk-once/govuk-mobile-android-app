package uk.gov.govuk.chat.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import dev.jeziellago.compose.markdowntext.MarkdownText
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Markdown(
    text: String,
    talkbackText: String,
    onMarkdownLinkClicked: (String, String) -> Unit,
    markdownLinkType: String,
    modifier: Modifier = Modifier
) {
    // Convert h1-h6 headers to bold
    val headerRegex = "(#+ *)(.*)(\n)".toRegex()
    val headerReplacement = "$3\\*\\*$2\\*\\*$3$3" // \n**header text**\n\n

    MarkdownText(
        markdown = headerRegex.replace(text, headerReplacement),
        linkColor = GovUkTheme.colourScheme.textAndIcons.chatBotLinkText,
        style = TextStyle(
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            fontSize = GovUkTheme.typography.bodyRegular.fontSize,
            fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
            fontWeight = GovUkTheme.typography.bodyRegular.fontWeight,
            lineHeight = GovUkTheme.typography.bodyRegular.lineHeight
        ),
        enableSoftBreakAddsNewLine = false,
        enableUnderlineForLink = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
            .semantics {
                contentDescription = talkbackText
            },
        onLinkClicked = { url ->
            onMarkdownLinkClicked(markdownLinkType, url)
        }
    )
}
