package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.util.toSpacedString

enum class RegistrationPlateStyle {
    FRONT,  // white plate
    REAR    // yellow plate
}

internal enum class RegistrationPlateSize {
    COMPACT, // reduced padding with 36dp minimum height
    REGULAR,
    LARGE
}

private data class RegistrationPlateDimensions(
    val textStyle: TextStyle,
    val radius: Dp,
    val padding: PaddingValues,
    val minHeight: Dp
)

@Composable
private fun resolveDimensions(
    size: RegistrationPlateSize
): RegistrationPlateDimensions = when (size) {
    RegistrationPlateSize.COMPACT -> RegistrationPlateDimensions(
        textStyle = GovUkTheme.typography.registrationPlateRegular,
        radius = 8.dp,
        padding = PaddingValues(horizontal = GovUkTheme.spacing.small, vertical = 5.dp),
        minHeight = 36.dp
    )
    RegistrationPlateSize.REGULAR -> RegistrationPlateDimensions(
        textStyle = GovUkTheme.typography.registrationPlateRegular,
        radius = 8.dp,
        padding = PaddingValues(GovUkTheme.spacing.small),
        minHeight = Dp.Unspecified
    )
    RegistrationPlateSize.LARGE -> RegistrationPlateDimensions(
        textStyle = GovUkTheme.typography.registrationPlateLarge,
        radius = 16.dp,
        padding = PaddingValues(GovUkTheme.spacing.medium),
        Dp.Unspecified
    )
}

@Composable
internal fun RegistrationPlate(
    registration: String,
    modifier: Modifier = Modifier,
    size: RegistrationPlateSize = RegistrationPlateSize.REGULAR,
    style: RegistrationPlateStyle = RegistrationPlateStyle.REAR
) {
    val accessibleNumberPlate = registration.toSpacedString()
    val altText = stringResource(id = R.string.registration_plate_alt_text, accessibleNumberPlate)
    val dimensions = resolveDimensions(size)
    val shape = RoundedCornerShape(dimensions.radius)

    val backgroundColour = when (style) {
        RegistrationPlateStyle.REAR -> GovUkTheme.colourScheme.surfaces.registrationPlate
        RegistrationPlateStyle.FRONT -> GovUkTheme.colourScheme.surfaces.registrationPlateFront
    }

    Box(
        modifier = modifier
            .heightIn(min = dimensions.minHeight)
            .background(
                color = backgroundColour,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = GovUkTheme.colourScheme.strokes.registrationPlate,
                shape = shape
            )
            .padding(dimensions.padding)
            .padding(top = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = registration,
            letterSpacing = TextUnit(0.05f, TextUnitType.Sp),
            style = dimensions.textStyle,
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
