package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class CardScreenshotTests {
    @PreviewTest
    @Preview(name = "HomeNotableDeathBannerCard", showBackground = true)
    @Composable
    fun HomeNotableDeathBannerCardScreenshots() {
        HomeNotableDeathBannerCardPreview()
    }

    @PreviewTest
    @Preview(name = "HomeNationalEmergencyBannerCard", showBackground = true)
    @Composable
    fun HomeNationalEmergencyBannerCardScreenshots() {
        HomeNationalEmergencyBannerCardPreview()
    }

    @PreviewTest
    @Preview(name = "HomeLocalEmergencyBannerCard", showBackground = true)
    @Composable
    fun HomeLocalEmergencyBannerCardScreenshots() {
        HomeLocalEmergencyBannerCardPreview()
    }

    @PreviewTest
    @Preview(name = "HomeInformationEmergencyBannerCard", showBackground = true)
    @Composable
    fun HomeInformationEmergencyBannerCardScreenshots() {
        HomeInformationEmergencyBannerCardPreview()
    }

    @PreviewTest
    @Preview(name = "SearchResultWithDescription", showBackground = true)
    @Composable
    fun SearchResultWithDescriptionScreenshots() {
        SearchResultWithDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "SearchResultWithoutDescription", showBackground = true)
    @Composable
    fun SearchResultWithoutDescriptionScreenshots() {
        SearchResultWithoutDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "QuarterlyFeedbackCard", showBackground = true)
    @Composable
    fun QuarterlyFeedbackCardScreenshots() {
        QuarterlyFeedbackCardPreview()
    }

    @PreviewTest
    @Preview(name = "NonTappableCard", showBackground = true)
    @Composable
    fun NonTappableCardScreenshots() {
        NonTappableCardPreview()
    }

    @PreviewTest
    @Preview(name = "CentredCardWithIconWithTitleAndDescription", showBackground = true)
    @Composable
    fun CentredCardWithIconWithTitleAndDescriptionScreenshots() {
        CentredCardWithIconWithTitleAndDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "CentredCardWithIconWithTitle", showBackground = true)
    @Composable
    fun CentredCardWithIconWithTitleScreenshots() {
        CentredCardWithIconWithTitlePreview()
    }

    @PreviewTest
    @Preview(name = "CentredCardWithIconWithDescription", showBackground = true)
    @Composable
    fun CentredCardWithIconWithDescriptionScreenshots() {
        CentredCardWithIconWithDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "NavigationCardWithTitleAndDescription", showBackground = true)
    @Composable
    fun NavigationCardWithTitleAndDescriptionScreenshots() {
        NavigationCardWithTitleAndDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "NavigationCardWithoutDescription", showBackground = true)
    @Composable
    fun NavigationCardWithoutDescriptionScreenshots() {
        NavigationCardWithoutDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "DrillInCard", showBackground = true)
    @Composable
    fun DrillInCardScreenshots() {
        DrillInCardPreview()
    }

    @PreviewTest
    @Preview(name = "DrillInCardDescription", showBackground = true)
    @Composable
    fun DrillInCardDescriptionScreenshots() {
        DrillInCardDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "AccountConnectionCard", showBackground = true)
    @Composable
    fun AccountConnectionCardScreenshots() {
        AccountConnectionCardPreview()
    }

    @PreviewTest
    @Preview(name = "LoaderCard", showBackground = true)
    @Composable
    fun LoaderCardScreenshots() {
        LoaderCardPreview()
    }
}