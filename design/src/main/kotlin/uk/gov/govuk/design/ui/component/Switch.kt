package uk.gov.govuk.design.ui.component

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.ui.theme.GovUkTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testDescription: String,
    modifier: Modifier = Modifier,
) {
    Switch(
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = GovUkTheme.colourScheme.surfaces.toggleHandle,
            checkedTrackColor = GovUkTheme.colourScheme.surfaces.switchOn,
            checkedBorderColor = GovUkTheme.colourScheme.strokes.switchOn,
            uncheckedThumbColor = GovUkTheme.colourScheme.surfaces.toggleHandle,
            uncheckedTrackColor = GovUkTheme.colourScheme.surfaces.switchOff,
            uncheckedBorderColor = GovUkTheme.colourScheme.strokes.switchOff
        ),
        modifier = modifier.semantics {
            testTagsAsResourceId = true
        }.testTag("toggle $testDescription")
    )
}

@Preview(showBackground = true)
@Composable
private fun ToggleSwitchOnPreview() {
    GovUkTheme {
        ToggleSwitch(
            checked = true,
            onCheckedChange = {},
            testDescription = "toggle"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToggleSwitchOffPreview() {
    GovUkTheme {
        ToggleSwitch(
            checked = false,
            onCheckedChange = {},
            testDescription = "toggle"
        )
    }
}
