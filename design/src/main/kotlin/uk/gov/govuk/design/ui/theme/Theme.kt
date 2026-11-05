package uk.gov.govuk.design.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object GovUkTheme {
    val colourScheme: GovUkColourScheme
        @Composable
        get() = LocalColourScheme.current
    val typography: GovUkTypography
        @Composable
        get() = LocalTypography.current
    val spacing: GovUkSpacing
        @Composable
        get() = LocalSpacing.current
    val numbers: GovUkNumbers
        @Composable
        get() = LocalNumbers.current
}

@Composable
fun GovUkTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalColourScheme provides colorScheme,
        LocalTypography provides Typography,
        LocalSpacing provides Spacing,
        LocalNumbers provides Numbers,
        content = content
    )
}
