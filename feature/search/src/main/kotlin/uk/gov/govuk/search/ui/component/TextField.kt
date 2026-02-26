package uk.gov.govuk.search.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

class SearchFieldActions(
    val onBack: () -> Unit,
    val onSearchTermChange: (TextFieldValue) -> Unit,
    val onSearch: () -> Unit,
    val onClear: () -> Unit
)

@Composable
fun SearchField(
    searchTerm: TextFieldValue,
    placeholder: String,
    actions: SearchFieldActions,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester()
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(GovUkTheme.colourScheme.surfaces.search),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = actions.onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = GovUkTheme.colourScheme.textAndIcons.link
            )
        }

        Box(Modifier.weight(1f)) {
            if (searchTerm.text.isEmpty()) {
                BodyRegularLabel(
                    text = placeholder,
                    color = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            }

            val contentDescSearchEntry =
                stringResource(uk.gov.govuk.search.R.string.content_desc_search_entry)
            BasicTextField(
                value = searchTerm,
                onValueChange = {
                    actions.onSearchTermChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .semantics {
                        contentDescription = contentDescSearchEntry
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { actions.onSearch() }),
                singleLine = true,
                textStyle = GovUkTheme.typography.bodyRegular.copy(
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                ),
                cursorBrush = SolidColor(GovUkTheme.colourScheme.textAndIcons.primary)
            )
        }

        if (searchTerm.text.isNotEmpty()) {
            TextButton(
                onClick = {
                    actions.onSearchTermChange(TextFieldValue(""))
                    actions.onClear()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(uk.gov.govuk.search.R.string.content_desc_clear),
                    tint = GovUkTheme.colourScheme.textAndIcons.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchHeaderPreview() {
    GovUkTheme {
        Box(Modifier.background(GovUkTheme.colourScheme.surfaces.homeHeader)) {
            SearchField(
                searchTerm = TextFieldValue(""),
                placeholder = "Search",
                actions = SearchFieldActions(
                    onBack = { },
                    onSearchTermChange = { },
                    onSearch = { },
                    onClear = { }
                )
            )
        }
    }
}
