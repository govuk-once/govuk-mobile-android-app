package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

@Composable
fun TitledSection(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Title3BoldLabel(
            text = title,
            modifier = Modifier.semantics { heading() }
        )

        SmallVerticalSpacer()

        SubheadlineRegularLabel(text = description)

        MediumVerticalSpacer()

        content()
    }
}