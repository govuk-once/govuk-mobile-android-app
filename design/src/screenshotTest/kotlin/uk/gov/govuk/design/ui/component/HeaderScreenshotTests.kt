package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class HeaderScreenshotTests {

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TabHeaderScreenshots() {
        TabHeaderPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FullScreenHeaderNoTextWithBackAndActionScreenshots() {
        FullScreenHeaderNoTextWithBackAndActionPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FullScreenHeaderBackAndActionScreenshots() {
        FullScreenHeaderBackAndActionPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FullScreenHeaderActionNoBackScreenshots() {
        FullScreenHeaderActionNoBackPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FullScreenHeaderBackNoActionScreenshots() {
        FullScreenHeaderBackNoActionPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FullScreenHeaderNoActionOrBackScreenshots() {
        FullScreenHeaderNoActionOrBackPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FullScreenHeaderLongTextNoActionOrBackScreenshots() {
        FullScreenHeaderLongTextNoActionOrBackPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FullScreenHeaderWithOverflowActionScreenshots() {
        FullScreenHeaderWithOverflowActionPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ChildPageHeaderNoTextWithBackAndActionScreenshots() {
        ChildPageHeaderNoTextWithBackAndActionPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ChildPageHeaderBackAndActionScreenshots() {
        ChildPageHeaderBackAndActionPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ChildPageHeaderActionNoBackScreenshots() {
        ChildPageHeaderActionNoBackPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ChildPageHeaderBackNoActionScreenshots() {
        ChildPageHeaderBackNoActionPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ChildPageHeaderNoActionOrBackScreenshots() {
        ChildPageHeaderNoActionOrBackPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ChildPageHeaderLongTextNoActionOrBackScreenshots() {
        ChildPageHeaderLongTextNoActionOrBackPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ModalHeaderScreenshots() {
        ModalHeaderPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ModalHeaderWithCloseScreenshots() {
        ModalHeaderWithClosePreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TitleScreenshots() {
        TitlePreview()
    }
}