package uk.gov.govuk.config.data

import uk.gov.govuk.config.data.remote.model.ChatBanner
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.PromoBanner
import uk.gov.govuk.config.data.remote.model.TermsAndConditions
import uk.gov.govuk.data.model.Result

interface ConfigRepo {
    val isAvailable: Boolean
    val chatPollIntervalSeconds: Double
    val minimumVersion: String
    val recommendedVersion: String
    val isSearchEnabled: Boolean
    val isRecentActivityEnabled: Boolean
    val isTopicsEnabled: Boolean
    val isNotificationsEnabled: Boolean
    val isLocalServicesEnabled: Boolean
    val isExternalBrowserEnabled: Boolean
    val isChatEnabled: Boolean
    val chatUrls: ChatUrls
    val refreshTokenExpirySeconds: Long?
    val emergencyBanners: List<EmergencyBanner>?
    val chatBanner: ChatBanner?
    val termsAndConditions: TermsAndConditions?
    val isFlexEnabled: Boolean
    val isDvlaLinkEnabled: Boolean
    val isMessagesEnabled: Boolean
    val isTravelAlertsEnabled: Boolean
    val isQuarterlySurveyEnabled: Boolean
    val dvlaUrls: DvlaUrls?
    val promoBanners: List<PromoBanner>?

    suspend fun initConfig(): Result<Unit>
    suspend fun activateRemoteConfig(): Boolean
    suspend fun refreshRemoteConfig()
    suspend fun clearRemoteConfigValues()
}
