package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class CountryOptionsBottomSheetScreenshotTests {
    @PreviewTest
    @Preview(name = "CountryOptionsBottomSheetContent", showBackground = true)
    @Composable
    fun CountryOptionsBottomSheetContentScreenshots() {
        CountryOptionsBottomSheetContentPreview()
    }
}