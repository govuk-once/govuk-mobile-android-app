package uk.gov.govuk.widgets.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.design.ui.component.HomeBannerCard
import uk.gov.govuk.design.ui.model.EmergencyBannerUiType

@Composable
fun EmergencyBanner(
    emergencyBanner: EmergencyBanner,
    onClick: (text: String, url: String?) -> Unit,
    launchBrowser: (url: String) -> Unit,
    onSuppressClick: (id: String, text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val analyticsText = emergencyBanner.link?.title ?: emergencyBanner.id
    val linkUrl = emergencyBanner.link?.url

    HomeBannerCard(
        modifier = modifier,
        title = emergencyBanner.title,
        description = emergencyBanner.body,
        linkTitle = emergencyBanner.link?.title,
        isDismissible = emergencyBanner.allowsDismissal,
        dismissAltText = emergencyBanner.dismissAltText,
        type = (emergencyBanner.type ?: EmergencyBannerType.INFORMATION).toEmergencyBannerUiType(),
        onClick = if (linkUrl != null) {
            {
                onClick(analyticsText, linkUrl)
                launchBrowser(linkUrl)
            }
        } else {
            null
        },
        onSuppressClick = { onSuppressClick(emergencyBanner.id, analyticsText) }
    )
}

fun EmergencyBannerType.toEmergencyBannerUiType() = when (this) {
    EmergencyBannerType.NOTABLE_DEATH -> EmergencyBannerUiType.NOTABLE_DEATH
    EmergencyBannerType.NATIONAL_EMERGENCY -> EmergencyBannerUiType.NATIONAL_EMERGENCY
    EmergencyBannerType.LOCAL_EMERGENCY -> EmergencyBannerUiType.LOCAL_EMERGENCY
    EmergencyBannerType.INFORMATION -> EmergencyBannerUiType.INFORMATION
}
