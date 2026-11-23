package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class LocalWidgetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NoLocalAuthorityScreenshots() {
        NoLocalAuthorityPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun UnitaryLocalAuthorityScreenshots() {
        UnitaryLocalAuthorityPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TwoTierLocalAuthorityScreenshots() {
        TwoTierLocalAuthorityPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun EditMenuScreenshots() {
        EditMenuPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun EditMenuTalkBackScreenshots() {
        EditMenuTalkBackPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun UnitaryRemoveConfirmationDialogScreenshots() {
        UnitaryRemoveConfirmationDialogPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TwoTierRemoveConfirmationDialogScreenshots() {
        TwoTierRemoveConfirmationDialogPreview()
    }
}
