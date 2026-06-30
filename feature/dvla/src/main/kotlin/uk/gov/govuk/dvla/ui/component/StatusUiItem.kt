package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.CountdownBarListItem
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.dvla.ui.model.StatusStyle
import uk.gov.govuk.dvla.ui.model.StatusUiModel

@Composable
internal fun StatusUiItem(
    launchBrowser: (text: String, url: String) -> Unit,
    statusUiModel: StatusUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (statusUiModel) {
            is StatusUiModel.StatusRow -> {
                StatusListItem(
                    title = statusUiModel.statusRowUi.title,
                    description = statusUiModel.statusRowUi.description,
                    iconStyle = statusUiModel.statusRowUi.iconStyle,
                    footerContent = {
                        statusUiModel.statusRowUi.style?.let { style ->
                            when (val style = style) {
                                is StatusStyle.ActionButton -> {
                                    MediumVerticalSpacer()

                                    StatusButton(
                                        text = style.text,
                                        onClick = { launchBrowser(style.text.displayText, style.url) },
                                        isPrimary = style.isPrimary,
                                        caption = style.caption
                                    )
                                }
                            }
                        }
                    }
                )
            }

            is StatusUiModel.CountdownRow -> {
                CountdownBarListItem(
                    title = statusUiModel.countdownBarUi.title,
                    topText = statusUiModel.countdownBarUi.topText,
                    percentage = statusUiModel.countdownBarUi.percentage,
                    bottomText = statusUiModel.countdownBarUi.bottomText,
                    footerContent = {
                        statusUiModel.countdownBarUi.style?.let { style ->
                            when (val style = style) {
                                is StatusStyle.ActionButton -> {
                                    MediumVerticalSpacer()

                                    StatusButton(
                                        text = style.text,
                                        onClick = { launchBrowser(style.text.displayText, style.url)},
                                        isPrimary = style.isPrimary,
                                        caption = style.caption
                                    )
                                }
                            }
                        }
                    }
                )
            }

            is StatusUiModel.InfoRow -> {
                InfoStatusItem(
                    title = statusUiModel.infoRowUi.title,
                    subtitle = statusUiModel.infoRowUi.subtitle,
                    icon = statusUiModel.infoRowUi.icon
                )
            }

            is StatusUiModel.NoStatus -> { /* Show nothing */ }
        }
    }
}
