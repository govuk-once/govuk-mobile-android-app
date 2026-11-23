package uk.gov.govuk.chat.ui.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Question(
    question: String,
    modifier: Modifier = Modifier,
    onCopyText: ((String) -> Unit)? = null
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(modifier = Modifier.padding(start = 60.dp)) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = GovUkTheme.colourScheme.surfaces.chatUserMessageBackground,
                    contentColor = GovUkTheme.colourScheme.textAndIcons.chatUserMessageText
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = if (onCopyText != null) {
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
                } else {
                    Modifier
                }
            ) {
                val altText = "${stringResource(R.string.question_alt)} $question"

                BodyRegularLabel(
                    text = question,
                    color = GovUkTheme.colourScheme.textAndIcons.chatUserMessageText,
                    modifier = Modifier
                        .padding(GovUkTheme.spacing.medium)
                        .talkBackText(altText)
                )
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = pressOffset.x.roundToInt(),
                            y = pressOffset.y.roundToInt()
                        )
                    }
            ) {
                DropdownMenu(
                    expanded = showContextMenu,
                    onDismissRequest = { showContextMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.copy_text)) },
                        onClick = {
                            showContextMenu = false
                            onCopyText?.invoke(question)
                        }
                    )
                }
            }
        }
    }
}
