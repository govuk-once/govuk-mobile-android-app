package uk.gov.govuk.chat.ui.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.chat.parser.MarkdownParser
import uk.gov.govuk.chat.parser.PlainTextExtractor
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Answer(
    answer: String,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier,
    sources: List<String>? = null,
    onCopyText: ((String) -> Unit)? = null
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    val parser = remember { MarkdownParser() }
    val warningText = stringResource(R.string.bot_sources_header_text)
    val sourcesHeaderText = stringResource(R.string.bot_sources_list_description)

    Box {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.chatBotMessageBackground,
                contentColor = GovUkTheme.colourScheme.textAndIcons.chatBotMessageText
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (onCopyText != null) {
                        Modifier
                            .indication(interactionSource, ripple())
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = { offset ->
                                        scope.launch {
                                            val press = PressInteraction.Press(offset)
                                            interactionSource.emit(press)
                                            interactionSource.emit(PressInteraction.Release(press))
                                        }
                                        pressOffset = offset
                                        showContextMenu = true
                                    }
                                )
                            }
                    } else Modifier
                )
        ) {
            MediumVerticalSpacer()

            Markdown(
                text = answer,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                markdownLinkType = Analytics.RESPONSE_LINK_CLICKED
            )

            if (!sources.isNullOrEmpty()) {
                Sources(
                    sources = sources,
                    onMarkdownLinkClicked = onMarkdownLinkClicked,
                    onSourcesExpanded = onSourcesExpanded
                )
            }

            MediumVerticalSpacer()
        }

        Box(
            modifier = Modifier
                .offset(
                    x = with(density) { pressOffset.x.toDp() },
                    y = with(density) { pressOffset.y.toDp() }
                )
        ) {
            DropdownMenu(
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.copy_text)) },
                    onClick = {
                        showContextMenu = false
                        val fullText = buildCopyText(
                            answer = answer,
                            warningText = warningText,
                            sourcesHeaderText = sourcesHeaderText,
                            sources = sources,
                            parser = parser
                        )
                        onCopyText?.invoke(fullText)
                    }
                )
            }
        }
    }
}

private fun buildCopyText(
    answer: String,
    warningText: String,
    sourcesHeaderText: String,
    sources: List<String>?,
    parser: MarkdownParser
): String {
    val answerElements = parser.parse(answer)
    val answerPlainText = PlainTextExtractor.extractAll(answerElements)

    return buildString {
        append(answerPlainText)

        if (!sources.isNullOrEmpty()) {
            append("\n\n")
            append(warningText)
            append("\n\n")
            append(sourcesHeaderText)
            append("\n")

            sources.forEach { source ->
                val sourceElements = parser.parse(source)
                val sourcePlainText = PlainTextExtractor.extractAll(sourceElements)
                append("\n")
                append(sourcePlainText)
            }
        }
    }
}
