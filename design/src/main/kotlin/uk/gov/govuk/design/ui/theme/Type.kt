package uk.gov.govuk.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import uk.gov.govuk.design.R

data class GovUkTypography(
    val titleLargeBold: TextStyle,
    val titleLargeRegular: TextStyle,
    val title1Bold: TextStyle,
    val title1Regular: TextStyle,
    val title2Bold: TextStyle,
    val title2Regular: TextStyle,
    val title3Bold: TextStyle,
    val title3Regular: TextStyle,
    val bodyBold: TextStyle,
    val bodyRegular: TextStyle,
    val calloutBold: TextStyle,
    val calloutRegular: TextStyle,
    val subheadlineBold: TextStyle,
    val subheadlineRegular: TextStyle,
    val footnoteBold: TextStyle,
    val footnoteRegular: TextStyle,
    val captionBold: TextStyle,
    val captionRegular: TextStyle
)

private val transport = FontFamily(
    Font(R.font.transport_bold, FontWeight.Bold),
    Font(R.font.transport_light, FontWeight.Light),
)

private val letterSpacing = TextUnit(0.05f, TextUnitType.Sp)

internal val Typography = GovUkTypography(
    titleLargeBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 41.sp,
        letterSpacing = letterSpacing
    ),
    titleLargeRegular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 34.sp,
        lineHeight = 41.sp,
        letterSpacing = letterSpacing
    ),
    title1Bold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = letterSpacing
    ),
    title1Regular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = letterSpacing
    ),
    title2Bold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = letterSpacing
    ),
    title2Regular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = letterSpacing
    ),
    title3Bold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = letterSpacing
    ),
    title3Regular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = letterSpacing
    ),
    bodyBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = letterSpacing
    ),
    bodyRegular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = letterSpacing
    ),
    calloutBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = letterSpacing
    ),
    calloutRegular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = letterSpacing
    ),
    subheadlineBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = letterSpacing
    ),
    subheadlineRegular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = letterSpacing
    ),
    footnoteBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = letterSpacing
    ),
    footnoteRegular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = letterSpacing
    ),
    captionBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = letterSpacing
    ),
    captionRegular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = letterSpacing
    )
)

internal val LocalTypography = staticCompositionLocalOf {
    GovUkTypography(
        titleLargeBold = TextStyle.Default,
        titleLargeRegular = TextStyle.Default,
        title1Bold = TextStyle.Default,
        title1Regular = TextStyle.Default,
        title2Bold = TextStyle.Default,
        title2Regular = TextStyle.Default,
        title3Bold = TextStyle.Default,
        title3Regular = TextStyle.Default,
        bodyBold = TextStyle.Default,
        bodyRegular = TextStyle.Default,
        calloutBold = TextStyle.Default,
        calloutRegular = TextStyle.Default,
        subheadlineBold = TextStyle.Default,
        subheadlineRegular = TextStyle.Default,
        footnoteBold = TextStyle.Default,
        footnoteRegular = TextStyle.Default,
        captionBold = TextStyle.Default,
        captionRegular = TextStyle.Default
    )
}
