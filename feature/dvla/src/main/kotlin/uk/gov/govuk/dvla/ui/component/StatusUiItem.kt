package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uk.gov.govuk.design.ui.component.CountdownBarListItem
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.StatusStyle
import uk.gov.govuk.dvla.ui.model.StatusUiModel

@Composable
internal fun StatusUiItem(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel,
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
    background: Color = GovUkTheme.colourScheme.surfaces.list
) {
    when (statusUiModel) {
        is StatusUiModel.StatusRow -> StatusRow(
            launchBrowser = launchBrowser,
            statusUiModel = statusUiModel,
            modifier = modifier,
            isLast = isLast,
            background = background
        )

        is StatusUiModel.CountdownRow -> CountdownRow(
            launchBrowser = launchBrowser,
            statusUiModel = statusUiModel,
            modifier = modifier,
            isLast = isLast,
            background = background
        )

        is StatusUiModel.InfoRow -> InfoRow(
            statusUiModel = statusUiModel,
            modifier = modifier,
            isLast = isLast,
            background = background
        )

        is StatusUiModel.LinkRow -> LinkRow(
            launchBrowser = launchBrowser,
            statusUiModel = statusUiModel,
            isLast = isLast,
            background = background
        )

        is StatusUiModel.NoStatus -> { /* Show nothing */ }
    }
}

@Composable
private fun StatusRow(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel.StatusRow,
    modifier: Modifier = Modifier,
    isLast: Boolean,
    background: Color,
) {
    StatusListItem(
        modifier = modifier,
        title = statusUiModel.statusRowUi.title,
        description = statusUiModel.statusRowUi.description,
        iconStyle = statusUiModel.statusRowUi.iconStyle,
        isLast = isLast,
        background = background,
        footerContent = {
            statusUiModel.statusRowUi.style?.let { style ->
                Style(
                    launchBrowser = launchBrowser,
                    style = style
                )
            }
        }
    )
}

@Composable
private fun CountdownRow(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel.CountdownRow,
    modifier: Modifier = Modifier,
    isLast: Boolean,
    background: Color
) {
    CountdownBarListItem(
        title = statusUiModel.countdownBarUi.title,
        topText = statusUiModel.countdownBarUi.topText,
        percentage = statusUiModel.countdownBarUi.percentage,
        bottomText = statusUiModel.countdownBarUi.bottomText,
        modifier = modifier,
        isLast = isLast,
        background = background,
        footerContent = {
            statusUiModel.countdownBarUi.style?.let { style ->
                Style(
                    launchBrowser = launchBrowser,
                    style = style
                )
            }
        }
    )
}

@Composable
private fun InfoRow(
    statusUiModel: StatusUiModel.InfoRow,
    modifier: Modifier = Modifier,
    isLast: Boolean,
    background: Color
) {
    InfoStatusItem(
        title = statusUiModel.infoRowUi.title,
        modifier = modifier,
        subtitle = statusUiModel.infoRowUi.subtitle,
        icon = statusUiModel.infoRowUi.icon,
        isLast = isLast,
        background = background
    )
}

@Composable
private fun LinkRow(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel.LinkRow,
    modifier: Modifier = Modifier,
    isLast: Boolean,
    background: Color
) {
    LinkStatusItem(
        title = statusUiModel.linkRowUi.title,
        text = statusUiModel.linkRowUi.text,
        onClick = {
            launchBrowser(
                statusUiModel.linkRowUi.text.displayText,
                statusUiModel.linkRowUi.url
            )
        },
        modifier = modifier,
        isLast = isLast,
        background = background
    )
}

@Composable
private fun Style(
    launchBrowser: (text: String, url: String) -> Unit,
    style: StatusStyle
) {
    when (val style = style) {
        is StatusStyle.ActionButton ->
            StatusButton(
                text = style.text,
                onClick = { launchBrowser(style.text.displayText, style.url) },
                isPrimary = style.isPrimary,
                caption = style.caption
            )
        is StatusStyle.Caption ->
            Caption(text = style.text)
    }
}
