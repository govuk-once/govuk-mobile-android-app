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
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalSelectViewModel
import uk.govuk.app.local.R
import uk.govuk.app.local.domain.model.Address

@Composable
internal fun LocalAddressSelectRoute(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onLocalAuthoritySelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LocalSelectViewModel = hiltViewModel()

    LocalAddressSelectScreen(
        onBack = onBack,
        onPageView = { viewModel.onSelectByAddressPageView() },
        onCancel = onCancel,
        onSlugSelect = { buttonText, slug ->
            viewModel.updateLocalAuthority(buttonText, slug)
            onLocalAuthoritySelected()
        },
        addresses = viewModel.addresses,
        modifier = modifier
    )
}

@Composable
private fun LocalAddressSelectScreen(
    onBack: () -> Unit,
    onPageView: () -> Unit,
    onCancel: () -> Unit,
    onSlugSelect: (String, String) -> Unit,
    addresses: List<Address>,
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
                text = stringResource(R.string.local_select_address_title),
                modifier = Modifier.semantics { heading() }
            )
            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(R.string.local_select_address_description)
            )

            MediumVerticalSpacer()

            RadioButtonGroup(
                addresses,
                onSlugChange = { slug -> selectedSlug = slug }
            )
        }
    }
}

@Composable
private fun RadioButtonGroup(
    radioOptions: List<Address>,
    onSlugChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (selectedOption, onOptionSelected) = rememberSaveable { mutableStateOf("") }

    Column(modifier.selectableGroup()) {
        radioOptions.forEachIndexed { index, address ->
            MediumVerticalSpacer()

            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (address.address == selectedOption),
                        onClick = {
                            onSlugChange(address.slug)
                            onOptionSelected(address.address) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (address.address == selectedOption),
                    onClick = null,
                    colors = RadioButtonColors(
                        selectedColor = GovUkTheme.colourScheme.surfaces.radioSelected,
                        unselectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected,
                        disabledSelectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected,
                        disabledUnselectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected
                    )
                )
                BodyRegularLabel(
                    text = address.address,
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

@Composable
private fun BottomNavBar(
    selectedSlug: String,
    onSlugSelect: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(R.string.local_select_address_confirm)

        FixedPrimaryButton(
            text = buttonText,
            enabled = selectedSlug.isNotEmpty(),
            onClick = { onSlugSelect(buttonText, selectedSlug) }
        )

        LargeVerticalSpacer()
    }
}

@Preview
@Composable
private fun LocalAddressSelectScreenPreview() {
    GovUkTheme {
        LocalAddressSelectScreen(
            onBack = {},
            onPageView = {},
            onCancel = {},
            onSlugSelect = { _, _ ->},
            addresses = listOf(
                Address(
                    address = "1 Dorset Street, BH22 8UB",
                    slug = "dorset",
                    name = "Dorset County Council"
                ),
                Address(
                    address = "42 Bournemouth Avenue, BH22 8UB",
                    slug = "bournemouth-christchurch-poole",
                    name = "Bournemouth, Christchurch, and Poole"
                )
            )
        )
    }
}
