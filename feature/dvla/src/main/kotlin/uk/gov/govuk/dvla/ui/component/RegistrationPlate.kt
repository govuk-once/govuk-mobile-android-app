package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.util.toSpacedString

@Composable
internal fun RegistrationPlate(
    registration: String,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false,
) {
    val accessibleNumberPlate = registration.toSpacedString()
    val altText = stringResource(id = R.string.registration_plate_alt_text, accessibleNumberPlate)
    val textStyle =
        if (isLarge) GovUkTheme.typography.registrationPlateLarge else GovUkTheme.typography.registrationPlateRegular
    val radius = if (isLarge) 16.dp else 8.dp
    val padding = if (isLarge) GovUkTheme.spacing.medium else GovUkTheme.spacing.small

    Box(
        modifier = modifier
            .background(
                color = GovUkTheme.colourScheme.surfaces.registrationPlate,
                shape = RoundedCornerShape(radius)
            )
            .border(
                width = 1.dp,
                color = GovUkTheme.colourScheme.strokes.registrationPlate,
                shape = RoundedCornerShape(radius)
            )
            .padding(padding)
            .padding(top = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = registration,
            letterSpacing = TextUnit(0.05f, TextUnitType.Sp),
            style = textStyle,
            color = GovUkTheme.colourScheme.textAndIcons.registrationPlateText,
            modifier = Modifier.semantics {
                contentDescription = altText
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun RegistrationPlatePreview() {
    GovUkTheme {
        RegistrationPlate("TE5T PL8")
    }
}
