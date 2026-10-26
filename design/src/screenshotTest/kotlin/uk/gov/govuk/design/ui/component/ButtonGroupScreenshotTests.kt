package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class ButtonGroupScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FixedPrimaryButtonScreenshots() {
        FixedPrimaryButtonPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FixedSecondaryButtonScreenshots() {
        FixedSecondaryButtonPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun VerticalButtonGroupScreenshots() {
        VerticalButtonGroupPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun HorizontalButtonGroupScreenshots() {
        HorizontalButtonGroupPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun VerticalDestructiveButtonGroupScreenshots() {
        VerticalDestructiveButtonGroupPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun HorizontalDestructiveButtonGroupScreenshots() {
        HorizontalDestructiveButtonGroupPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun HorizontalConnectedButtonGroupScreenshots() {
        HorizontalConnectedButtonGroupPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun VerticalConnectedButtonGroupScreenshots() {
        VerticalConnectedButtonGroupPreview()
    }
}