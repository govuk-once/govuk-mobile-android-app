package uk.gov.govuk.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GovUkNumbers(
    val cornerAndroidList: Dp
)

internal val Numbers = GovUkNumbers(
    cornerAndroidList = 12.dp
)

internal val LocalNumbers = staticCompositionLocalOf {
    GovUkNumbers(
        cornerAndroidList = Dp.Unspecified
    )
}
