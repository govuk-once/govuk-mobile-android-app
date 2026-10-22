package uk.gov.govuk.terms.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class TermsScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TermsNewUserPreviewScreenshots() {
        TermsNewUserPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TermsUpdatedPreviewScreenshots() {
        TermsUpdatedPreview()
    }
}