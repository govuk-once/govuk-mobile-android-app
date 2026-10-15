package uk.gov.govuk.terms.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.login.ui.BiometricPreview

internal class TermsScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "TermsNewUser", showBackground = true)
    @Composable
    fun TermsNewUserPreviewScreenshots() {
        TermsNewUserPreview()
    }

    @PreviewTest
    @Preview(name = "TermsUpdated", showBackground = true)
    @Composable
    fun TermsUpdatedPreviewScreenshots() {
        TermsUpdatedPreview()
    }
}