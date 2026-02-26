package uk.govuk.app.local.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalSelectViewModel
import uk.govuk.app.local.R
import uk.govuk.app.local.domain.model.LocalAuthority

@Composable
internal fun LocalAuthoritySelectRoute(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onLocalAuthoritySelected: () -> Unit,
    onSelectByAddress: () -> Unit,
    postcode: String,
    modifier: Modifier = Modifier,
) {
    val viewModel: LocalSelectViewModel = hiltViewModel()

    LocalAuthoritySelectScreen(
        onBack = onBack,
        onPageView = { viewModel.onSelectByLocalAuthorityPageView() },
        onCancel = onCancel,
        onSelectByAddress = { buttonText ->
            viewModel.onSelectByAddressButtonClick(buttonText)
            onSelectByAddress() },
        onSlugSelect = { buttonText, slug ->
            viewModel.updateLocalAuthority(buttonText, slug)
            onLocalAuthoritySelected()
        },
        postcode = postcode,
        localAuthorities = viewModel.localAuthorities,
        modifier = modifier
    )
}

@Composable
private fun LocalAuthoritySelectScreen(
    onBack: () -> Unit,
    onPageView: () -> Unit,
    onCancel: () -> Unit,
    onSelectByAddress: (String) -> Unit,
    onSlugSelect: (String, String) -> Unit,
    postcode: String,
    localAuthorities: List<LocalAuthority>,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    var selectedSlug by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            LocalFullScreenHeader(onBack = onBack, onCancel = onCancel)
        },
        bottomBar = {
            BottomNavBar(
                onSelectByAddress = onSelectByAddress,
                selectedSlug = selectedSlug,
                onSlugSelect = onSlugSelect,
                modifier = Modifier
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = 1f
                    }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            SmallVerticalSpacer()
            Title1BoldLabel(
                text = stringResource(R.string.local_select_council_title),
                modifier = Modifier.semantics { heading() }
            )
            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(R.string.local_select_council_description, postcode)
            )

            MediumVerticalSpacer()

            RadioButtonGroup(
                localAuthorities,
                onSlugChange = { slug -> selectedSlug = slug }
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onSelectByAddress: (String) -> Unit,
    onSlugSelect: (String, String) -> Unit,
    selectedSlug: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val confirmButtonText = stringResource(R.string.local_select_council_confirm)
        val selectByAddressButtonText = stringResource(R.string.local_select_by_address)

        FixedDoubleButtonGroup(
            primaryText = confirmButtonText,
            onPrimary = { onSlugSelect(confirmButtonText, selectedSlug) },
            secondaryText = selectByAddressButtonText,
            onSecondary = { onSelectByAddress(selectByAddressButtonText) },
            modifier = modifier,
            primaryEnabled = selectedSlug.isNotEmpty()
        )
    }
}

@Composable
private fun RadioButtonGroup(
    radioOptions: List<LocalAuthority>,
    onSlugChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (selectedOption, onOptionSelected) = rememberSaveable { mutableStateOf("") }

    Column(modifier.selectableGroup()) {
        radioOptions.forEachIndexed { index, localAuthority ->
            MediumVerticalSpacer()

            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (localAuthority.slug == selectedOption),
                        onClick = {
                            onSlugChange(localAuthority.slug)
                            onOptionSelected(localAuthority.slug) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (localAuthority.slug == selectedOption),
                    onClick = null,
                    colors = RadioButtonColors(
                        selectedColor = GovUkTheme.colourScheme.surfaces.radioSelected,
                        unselectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected,
                        disabledSelectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected,
                        disabledUnselectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected
                    )
                )
                BodyRegularLabel(
                    text = localAuthority.name,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                )
            }

            MediumVerticalSpacer()

            if (index < radioOptions.lastIndex) {
                RadioDivider()
            }
        }
    }
}


@Composable
private fun RadioDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = GovUkTheme.colourScheme.strokes.radioDivider
    )
}

@Preview
@Composable
private fun LocalAuthoritySelectScreenPreview() {
    GovUkTheme {
        LocalAuthoritySelectScreen(
            onBack = {},
            onPageView = {},
            onCancel = {},
            onSelectByAddress = {},
            onSlugSelect = { _, _ ->},
            postcode = "E18QS",
            localAuthorities = listOf(
                LocalAuthority(
                    name = "Dorset County Council",
                    url = "https://www.example.com",
                    slug = "dorset",
                    parent = null
                ),
                LocalAuthority(
                    name = "Bournemouth, Christchurch, and Poole",
                    url = "https://www.example.com",
                    slug = "bournemouth-christchurch-poole",
                    parent = null
                )
            )
        )
    }
}
