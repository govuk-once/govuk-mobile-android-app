package uk.gov.govuk.chat.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Question(
    question: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.chatUserMessageBackground,
                contentColor = GovUkTheme.colourScheme.textAndIcons.chatUserMessageText
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.padding(start = 60.dp)
        ) {
            val altText = "${stringResource(R.string.answer_alt)} $question"

            BodyRegularLabel(
                text = question,
                color = GovUkTheme.colourScheme.textAndIcons.chatUserMessageText,
                modifier = Modifier
                    .padding(GovUkTheme.spacing.medium)
                    .clearAndSetSemantics {
                        contentDescription = altText
                    }
            )
        }
    }
}
