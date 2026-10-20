package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class HeaderScreenshotTests {

    @PreviewTest
    @Preview(name = "TabHeader", showBackground = true)
    @Composable
    fun TabHeaderScreenshots() {
        TabHeaderPreview()
    }

    @PreviewTest
    @Preview(name = "FullScreenHeaderNoTextWithBackAndAction", showBackground = true)
    @Composable
    fun FullScreenHeaderNoTextWithBackAndActionScreenshots() {
        FullScreenHeaderNoTextWithBackAndActionPreview()
    }

    @PreviewTest
    @Preview(name = "FullScreenHeaderBackAndAction", showBackground = true)
    @Composable
    fun FullScreenHeaderBackAndActionScreenshots() {
        FullScreenHeaderBackAndActionPreview()
    }

    @PreviewTest
    @Preview(name = "FullScreenHeaderActionNoBack", showBackground = true)
    @Composable
    fun FullScreenHeaderActionNoBackScreenshots() {
        FullScreenHeaderActionNoBackPreview()
    }

    @PreviewTest
    @Preview(name = "FullScreenHeaderBackNoAction", showBackground = true)
    @Composable
    fun FullScreenHeaderBackNoActionScreenshots() {
        FullScreenHeaderBackNoActionPreview()
    }

    @PreviewTest
    @Preview(name = "FullScreenHeaderNoActionOrBack", showBackground = true)
    @Composable
    fun FullScreenHeaderNoActionOrBackScreenshots() {
        FullScreenHeaderNoActionOrBackPreview()
    }

    @PreviewTest
    @Preview(name = "FullScreenHeaderLongTextNoActionOrBack", showBackground = true)
    @Composable
    fun FullScreenHeaderLongTextNoActionOrBackScreenshots() {
        FullScreenHeaderLongTextNoActionOrBackPreview()
    }

    @PreviewTest
    @Preview(name = "FullScreenHeaderWithOverflowAction", showBackground = true)
    @Composable
    fun FullScreenHeaderWithOverflowActionScreenshots() {
        FullScreenHeaderWithOverflowActionPreview()
    }

    @PreviewTest
    @Preview(name = "ChildPageHeaderNoTextWithBackAndAction", showBackground = true)
    @Composable
    fun ChildPageHeaderNoTextWithBackAndActionScreenshots() {
        ChildPageHeaderNoTextWithBackAndActionPreview()
    }

    @PreviewTest
    @Preview(name = "ChildPageHeaderBackAndAction", showBackground = true)
    @Composable
    fun ChildPageHeaderBackAndActionScreenshots() {
        ChildPageHeaderBackAndActionPreview()
    }

    @PreviewTest
    @Preview(name = "ChildPageHeaderActionNoBack", showBackground = true)
    @Composable
    fun ChildPageHeaderActionNoBackScreenshots() {
        ChildPageHeaderActionNoBackPreview()
    }

    @PreviewTest
    @Preview(name = "ChildPageHeaderBackNoAction", showBackground = true)
    @Composable
    fun ChildPageHeaderBackNoActionScreenshots() {
        ChildPageHeaderBackNoActionPreview()
    }

    @PreviewTest
    @Preview(name = "ChildPageHeaderNoActionOrBack", showBackground = true)
    @Composable
    fun ChildPageHeaderNoActionOrBackScreenshots() {
        ChildPageHeaderNoActionOrBackPreview()
    }

    @PreviewTest
    @Preview(name = "ChildPageHeaderLongTextNoActionOrBack", showBackground = true)
    @Composable
    fun ChildPageHeaderLongTextNoActionOrBackScreenshots() {
        ChildPageHeaderLongTextNoActionOrBackPreview()
    }

    @PreviewTest
    @Preview(name = "ModalHeader", showBackground = true)
    @Composable
    fun ModalHeaderScreenshots() {
        ModalHeaderPreview()
    }

    @PreviewTest
    @Preview(name = "ModalHeaderWithClose", showBackground = true)
    @Composable
    fun ModalHeaderWithCloseScreenshots() {
        ModalHeaderWithClosePreview()
    }

    @PreviewTest
    @Preview(name = "Title", showBackground = true)
    @Composable
    fun TitleScreenshots() {
        TitlePreview()
    }
}