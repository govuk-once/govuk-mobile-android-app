package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ButtonGroupScreenshotTests {
    @PreviewTest
    @Preview(name = "FixedPrimaryButton", showBackground = true)
    @Composable
    fun FixedPrimaryButtonScreenshots() {
        FixedPrimaryButtonPreview()
    }

    @PreviewTest
    @Preview(name = "FixedSecondaryButton", showBackground = true)
    @Composable
    fun FixedSecondaryButtonScreenshots() {
        FixedSecondaryButtonPreview()
    }

    @PreviewTest
    @Preview(name = "VerticalButtonGroup", showBackground = true)
    @Composable
    fun VerticalButtonGroupScreenshots() {
        VerticalButtonGroupPreview()
    }

    @PreviewTest
    @Preview(name = "HorizontalButtonGroup", showBackground = true)
    @Composable
    fun HorizontalButtonGroupScreenshots() {
        HorizontalButtonGroupPreview()
    }

    @PreviewTest
    @Preview(name = "VerticalDestructiveButtonGroup", showBackground = true)
    @Composable
    fun VerticalDestructiveButtonGroupScreenshots() {
        VerticalDestructiveButtonGroupPreview()
    }

    @PreviewTest
    @Preview(name = "HorizontalDestructiveButtonGroup", showBackground = true)
    @Composable
    fun HorizontalDestructiveButtonGroupScreenshots() {
        HorizontalDestructiveButtonGroupPreview()
    }

    @PreviewTest
    @Preview(name = "HorizontalConnectedButtonGroup", showBackground = true)
    @Composable
    fun HorizontalConnectedButtonGroupScreenshots() {
        HorizontalConnectedButtonGroupPreview()
    }

    @PreviewTest
    @Preview(name = "VerticalConnectedButtonGroup", showBackground = true)
    @Composable
    fun VerticalConnectedButtonGroupScreenshots() {
        VerticalConnectedButtonGroupPreview()
    }
}