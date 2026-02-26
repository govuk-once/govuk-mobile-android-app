package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun CentreAlignedScreen(
    modifier: Modifier = Modifier,
    screenContent: @Composable ColumnScope.() -> Unit,
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null,
    footerContent: @Composable () -> Unit
) {
    Column(modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            Spacer(Modifier.weight(1f))

            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(
                        horizontal = GovUkTheme.spacing.medium,
                        vertical = GovUkTheme.spacing.large
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                screenContent()
            }

            Spacer(Modifier.weight(1f))

            bottomContent?.let { bottomContent ->
                bottomContent()
            }
        }

        footerContent()
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    accessibilityText: String = ""
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(36.dp)
                .semantics {
                    contentDescription = accessibilityText
                }
            ,
            color = GovUkTheme.colourScheme.surfaces.primary
        )
    }
}