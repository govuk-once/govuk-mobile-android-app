package uk.gov.govuk.search.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.search.R

@Composable
fun SearchWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchBarText = stringResource(R.string.search_bar_text)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(GovUkTheme.colourScheme.surfaces.search)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick(searchBarText)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.secondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        BodyRegularLabel(
            text = searchBarText,
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )
    }
}

@Preview
@Composable
private fun SearchWidgetPreview() {
    GovUkTheme {
        SearchWidget(onClick = { })
    }
}