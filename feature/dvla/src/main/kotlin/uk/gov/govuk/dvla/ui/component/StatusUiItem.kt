package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.CountdownBarListItem
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.dvla.ui.model.StatusStyle
import uk.gov.govuk.dvla.ui.model.StatusUiModel

@Composable
internal fun StatusUiItem(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel,
    modifier: Modifier = Modifier
) {
    when (statusUiModel) {
        is StatusUiModel.StatusRow -> StatusRow(
            launchBrowser = launchBrowser,
            statusUiModel = statusUiModel,
            modifier = modifier
        )

        is StatusUiModel.CountdownRow -> CountdownRow(
            launchBrowser = launchBrowser,
            statusUiModel = statusUiModel,
            modifier = modifier
        )

        is StatusUiModel.InfoRow -> InfoRow(
            statusUiModel = statusUiModel,
            modifier = modifier
        )

        is StatusUiModel.LinkRow -> LinkRow(
            launchBrowser = launchBrowser,
            statusUiModel = statusUiModel,
        )

        is StatusUiModel.NoStatus -> { /* Show nothing */ }
    }
}

@Composable
private fun StatusRow(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel.StatusRow,
    modifier: Modifier = Modifier
) {
    StatusListItem(
        modifier = modifier,
        title = statusUiModel.statusRowUi.title,
        description = statusUiModel.statusRowUi.description,
        iconStyle = statusUiModel.statusRowUi.iconStyle,
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
    modifier: Modifier = Modifier
) {
    CountdownBarListItem(
        title = statusUiModel.countdownBarUi.title,
        topText = statusUiModel.countdownBarUi.topText,
        percentage = statusUiModel.countdownBarUi.percentage,
        bottomText = statusUiModel.countdownBarUi.bottomText,
        modifier = modifier,
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
    modifier: Modifier = Modifier
) {
    InfoStatusItem(
        title = statusUiModel.infoRowUi.title,
        modifier = modifier,
        subtitle = statusUiModel.infoRowUi.subtitle,
        icon = statusUiModel.infoRowUi.icon
    )
}

@Composable
private fun LinkRow(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel.LinkRow,
    modifier: Modifier = Modifier
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
    }
}
