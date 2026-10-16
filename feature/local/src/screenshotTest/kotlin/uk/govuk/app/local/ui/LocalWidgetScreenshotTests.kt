package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LocalWidgetScreenshotTests {
    @PreviewTest
    @Preview(name = "NoLocalAuthority", showBackground = true)
    @Composable
    fun NoLocalAuthorityScreenshots() {
        NoLocalAuthorityPreview()
    }

    @PreviewTest
    @Preview(name = "UnitaryLocalAuthority", showBackground = true)
    @Composable
    fun UnitaryLocalAuthorityScreenshots() {
        UnitaryLocalAuthorityPreview()
    }

    @PreviewTest
    @Preview(name = "TwoTierLocalAuthority", showBackground = true)
    @Composable
    fun TwoTierLocalAuthorityScreenshots() {
        TwoTierLocalAuthorityPreview()
    }
}