package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class LicenceCategoryDetailRouteScreenshotTest {

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun LicenceCategoryDetailScreenScreenshots() {
        LicenceCategoryDetailScreenPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun LicenceCategoryDetailNoIconScreenScreenshots() {
        LicenceCategoryDetailScreenNoIconPreview()
    }
}