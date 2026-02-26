package uk.gov.govuk.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.ExtraSmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.search.R

@Composable
internal fun SearchAutocomplete(
    searchTerm: String,
    suggestions: List<String>,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    if (suggestions.isNotEmpty()) {
        val heading = stringResource(R.string.search_autocomplete_heading)
        val searchLabel = stringResource(R.string.content_desc_search)
        val numberOfSuggestedSearches =
            pluralStringResource(
                id = R.plurals.number_of_suggested_searches,
                count = suggestions.size,
                suggestions.size
            )

        var searchesAnnouncement by remember { mutableStateOf("") }

        LaunchedEffect(numberOfSuggestedSearches) {
            searchesAnnouncement = numberOfSuggestedSearches
        }

        LazyColumn(
            modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = GovUkTheme.spacing.medium,
                            bottom = GovUkTheme.spacing.small
                        ).semantics {
                            liveRegion = LiveRegionMode.Polite
                            contentDescription = searchesAnnouncement
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BodyBoldLabel(
                        text = heading,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .focusable()
                            .semantics { heading() }
                    )
                }
            }
            items(suggestions) { suggestion ->
                Column {
                    ListDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearch(suggestion) }
                            .padding(
                                top = GovUkTheme.spacing.medium,
                                bottom = GovUkTheme.spacing.medium
                            )
                            .semantics {
                                onClick(label = searchLabel) { true }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = GovUkTheme.colourScheme.textAndIcons.secondary
                        )
                        ExtraSmallHorizontalSpacer()
                        Text(text = highlightSearchTerms(suggestion, searchTerm))
                    }
                }
            }
            item {
                ListDivider()
            }
        }
    }
}

@Composable
private fun highlightSearchTerms(suggestion: String, searchTerm: String): AnnotatedString {
    val suggestionWords = suggestion.split(" ")
    val searchTermWords = searchTerm.split(" ")
    val marker = '|'

    var parsedSuggestions: Array<String> = emptyArray<String>()
    searchTermWords.forEach { searchWord ->
        parsedSuggestions += suggestion.replace(searchWord, "$marker$searchWord$marker", ignoreCase = false)
    }

    val mappedVariants = mapSuggestionVariants(suggestionWords, parsedSuggestions).toMutableMap()
    mappedVariants.forEach { mappedVariant ->
        if (mappedVariant.value.size > 1) {
            fixMultipleMarkers(mappedVariant.value, marker)
            mappedVariants += mergeSuggestionVariants(mappedVariant, marker)
        }
    }

    return highlightVariants(suggestionWords, mappedVariants, marker)
}

private fun mapSuggestionVariants(
    suggestionWords: List<String>,
    parsedSuggestions: Array<String>
): Map<String, ArrayList<String>> {
    val mappedVariants = emptyMap<String, ArrayList<String>>().toMutableMap()

    suggestionWords.forEachIndexed  { index, suggestionWord ->
        val variants = ArrayList<String>()

        parsedSuggestions.forEach { parsedSuggestion ->
            val word = parsedSuggestion.split(" ")[index]

            if (!variants.contains(word) && suggestionWord != word) {
                variants += word
            }
        }
        mappedVariants += mapOf(
            suggestionWord to variants
        )
    }

    return mappedVariants
}

private fun mergeSuggestionVariants(
    mappedVariant: Map.Entry<String, ArrayList<String>>,
    marker: Char
): Map<String, ArrayList<String>> {
    var startIndex = mappedVariant.key.length - 1
    var endIndex = 0

    mappedVariant.value.forEach { variant ->
        val firstIndex = variant.indexOf(marker)
        val lastIndex = variant.lastIndexOf(marker)

        if (startIndex > firstIndex) { startIndex = firstIndex }
        if (endIndex < lastIndex) { endIndex = lastIndex }
    }

    val updatedVariants = ArrayList<String>()
    val updatedVariantsString = mappedVariant.key
        .replaceRange(startIndex, startIndex, marker.toString())
        .replaceRange(endIndex, endIndex, marker.toString())

    updatedVariants += updatedVariantsString

    return mapOf(mappedVariant.key to updatedVariants)
}

private fun fixMultipleMarkers(mappedVariants: ArrayList<String>, marker: Char) {
    mappedVariants.forEachIndexed { index, suggestion ->
        if (suggestion.split(marker).size > 3) {
            var startIndex = suggestion.indexOf(marker)
            var lastIndex = suggestion.lastIndexOf(marker)
            var newSuggestion = suggestion.substring(startIndex + 1, lastIndex + 1)
                .replace(marker.toString(), "")

            newSuggestion = "${suggestion.substring(0, startIndex + 1)}$newSuggestion${
                suggestion.substring(
                    lastIndex,
                    suggestion.length
                )
            }"

            mappedVariants[index] = newSuggestion
        } else {
            mappedVariants[index] = suggestion
        }
    }
}

@Composable
private fun highlightVariants(
    suggestionWords: List<String>,
    mappedVariants: Map<String, ArrayList<String>>,
    marker: Char
): AnnotatedString {
    val transport = FontFamily(
        Font(uk.gov.govuk.design.R.font.transport_bold, FontWeight.Bold),
        Font(uk.gov.govuk.design.R.font.transport_light, FontWeight.Light),
    )

    val normalStyle = SpanStyle(
        fontFamily = transport,
        fontSize = 17.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Bold,
        color = GovUkTheme.colourScheme.textAndIcons.primary,
    )

    val highlightStyle = SpanStyle(
        fontFamily = transport,
        fontSize = 17.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Light,
        color = GovUkTheme.colourScheme.textAndIcons.primary,
    )

    return buildAnnotatedString {
        suggestionWords.forEach { suggestionWord ->
            if (mappedVariants[suggestionWord] == null || mappedVariants[suggestionWord]!!.isEmpty()) {
                withStyle(style = normalStyle) {
                    append(suggestionWord)
                }
            } else {
                val word = mappedVariants[suggestionWord]!![0]
                word.split(marker).forEachIndexed { index, chunk ->
                    withStyle(style = if (index % 2 == 0) normalStyle else highlightStyle) {
                        append(chunk)
                    }
                }
            }
            append(" ")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightSingleSearchTerm1Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "pay employers PAYE",
                searchTerm = "pay"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightSingleSearchTerm2Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "payroll",
                searchTerm = "pay"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightSingleSearchTerm3Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "pay self assessment",
                searchTerm = "pay"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightSingleSearchTerm4Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "check your pay",
                searchTerm = "pay"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightSingleSearchTerm5Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "understanding your pay",
                searchTerm = "pay"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightDoubleSearchTerm1Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "pay your corporation tax bill",
                searchTerm = "pay tax"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightDoubleSearchTerm2Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "pay capital gain tax uk property",
                searchTerm = "pay tax"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightDoubleSearchTerm3Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "pay corporation tax",
                searchTerm = "pay tax"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightDoubleSearchTerm4Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "pay your corporation tax",
                searchTerm = "pay tax"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightDoubleSearchTerm5Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "pay tax",
                searchTerm = "pay tax"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightMultipleSearchTerms1Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "mouse house housey mousey",
                searchTerm = "hou ous"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightMultipleSearchTerms2Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "Android Android android",
                searchTerm = "and oi"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightMultipleSearchTerms3Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "Ho! Ho! Ho! said the mouse as it moved into a new house",
                searchTerm = "ou Ho ho"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HighlightMultipleSearchTerms4Preview() {
    GovUkTheme {
        Text(text = highlightSearchTerms(
                suggestion = "mouse house housey mousey mousehouse",
                searchTerm = "hou ous"
            )
        )
    }
}
