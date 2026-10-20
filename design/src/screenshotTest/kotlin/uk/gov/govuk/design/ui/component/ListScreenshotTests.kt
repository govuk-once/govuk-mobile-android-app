package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ListScreenshotTests {

    @PreviewTest
    @Preview(name = "InternalLinkListItem", showBackground = true)
    @Composable
    fun InternalLinkListItemScreenshots() {
        InternalLinkListItemPreview()
    }

    @PreviewTest
    @Preview(name = "InternalLinkListItemDescription", showBackground = true)
    @Composable
    fun InternalLinkListItemDescriptionScreenshots() {
        InternalLinkListItemDescriptionPreview()
    }

    @PreviewTest
    @Preview(name = "InternalLinkListItemStatus", showBackground = true)
    @Composable
    fun InternalLinkListItemStatusScreenshots() {
        InternalLinkListItemStatusPreview()
    }

    @PreviewTest
    @Preview(name = "InternalLinkListItemInfo", showBackground = true)
    @Composable
    fun InternalLinkListItemInfoScreenshots() {
        InternalLinkListItemInfoPreview()
    }

    @PreviewTest
    @Preview(name = "InternalLinkListItemButton", showBackground = true)
    @Composable
    fun InternalLinkListItemButtonScreenshots() {
        InternalLinkListItemButtonPreview()
    }

    @PreviewTest
    @Preview(name = "InternalLinkListItemShortButton", showBackground = true)
    @Composable
    fun InternalLinkListItemShortButtonScreenshots() {
        InternalLinkListItemShortButtonPreview()
    }

    @PreviewTest
    @Preview(name = "InternalLinkListItemTrailingIcon", showBackground = true)
    @Composable
    fun InternalLinkListItemTrailingIconScreenshots() {
        InternalLinkListItemTrailingIconPreview()
    }

    @PreviewTest
    @Preview(name = "InternalLinkListItemSimple", showBackground = true)
    @Composable
    fun InternalLinkListItemSimpleScreenshots() {
        InternalLinkListItemSimplePreview()
    }

    @PreviewTest
    @Preview(name = "ExternalLinkListItemDefault", showBackground = true)
    @Composable
    fun ExternalLinkListItemDefaultScreenshots() {
        ExternalLinkListItemDefaultPreview()
    }

    @PreviewTest
    @Preview(name = "ExternalLinkListItemIcon", showBackground = true)
    @Composable
    fun ExternalLinkListItemIconScreenshots() {
        ExternalLinkListItemIconPreview()
    }

    @PreviewTest
    @Preview(name = "ExternalLinkListItemButton", showBackground = true)
    @Composable
    fun ExternalLinkListItemButtonScreenshots() {
        ExternalLinkListItemButtonPreview()
    }
    @PreviewTest
    @Preview(name = "StatusListItem", showBackground = true)
    @Composable
    fun StatusListItemScreenshots() {
        StatusListItemPreview()
    }

    @PreviewTest
    @Preview(name = "StatusListItemWarning", showBackground = true)
    @Composable
    fun StatusListItemWarningScreenshots() {
        StatusListItemWarningPreview()
    }

    @PreviewTest
    @Preview(name = "StatusListItemNoTitle", showBackground = true)
    @Composable
    fun StatusListItemNoTitleScreenshots() {
        StatusListItemNoTitlePreview()
    }

    @PreviewTest
    @Preview(name = "AddressListItem", showBackground = true)
    @Composable
    fun AddressListItemScreenshots() {
        AddressListItemPreview()
    }

    @PreviewTest
    @Preview(name = "CountListItem", showBackground = true)
    @Composable
    fun CountListItemScreenshots() {
        CountListItemPreview()
    }

    @PreviewTest
    @Preview(name = "CountdownBarItemListItem", showBackground = true)
    @Composable
    fun CountdownBarItemListItemScreenshots() {
        CountdownBarItemListItemPreview()
    }

    @PreviewTest
    @Preview(name = "ToggleListItem", showBackground = true)
    @Composable
    fun ToggleListItemScreenshots() {
        ToggleListItemPreview()
    }
}