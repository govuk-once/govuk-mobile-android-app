package uk.gov.govuk.config.data.flags

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.extensions.isVersionLessThan

@Singleton
class FlagRepo @Inject constructor(
    private val debugEnabled: Boolean,
    private val debugFlags: DebugFlags,
    private val configRepo: ConfigRepo
) {
    fun isAppAvailable(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isAppAvailable,
        remoteFlag = configRepo.isAvailable
    )

    fun isForcedUpdate(
        appVersion: String
    ): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.minimumVersion?.let {
            appVersion.isVersionLessThan(it)
        },
        remoteFlag = appVersion.isVersionLessThan(configRepo.minimumVersion)
    )

    fun isRecommendUpdate(
        appVersion: String
    ): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.recommendedVersion?.let {
            appVersion.isVersionLessThan(it)
        },
        remoteFlag = appVersion.isVersionLessThan(configRepo.recommendedVersion)
    )

    fun isSearchEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isSearchEnabled,
        remoteFlag = configRepo.isSearchEnabled
    )

    fun isRecentActivityEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isRecentActivityEnabled,
        remoteFlag = configRepo.isRecentActivityEnabled
    )

    fun isTopicsEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isTopicsEnabled,
        remoteFlag = configRepo.isTopicsEnabled
    )

    fun isNotificationsEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isNotificationsEnabled,
        remoteFlag = configRepo.isNotificationsEnabled
    )

    fun isLocalServicesEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isLocalServicesEnabled,
        remoteFlag = configRepo.isLocalServicesEnabled
    )

    fun isExternalBrowserEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isExternalBrowserEnabled,
        remoteFlag = configRepo.isExternalBrowserEnabled
    )

    fun isChatEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isChatEnabled,
        remoteFlag = configRepo.isChatEnabled
    )

    fun isFlexEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isFlexEnabled,
        remoteFlag = configRepo.isFlexEnabled
    )

    fun isDvlaLinkEnabled(): Boolean = isFlexEnabled() &&
        isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isDvlaLinkEnabled,
            remoteFlag = false //  Not yet wired up to remote config, always off for prod builds!!!
        )

    fun isMessagesEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isMessagesEnabled,
//             remoteFlag = configRepo.isMessagesEnabled
        remoteFlag = false //  Always off for prod builds!!!
    )

    fun isTravelAlertsEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isTravelAlertsEnabled,
//            remoteFlag = configRepo.isTravelAlertsEnabled
        remoteFlag = false // Always off for prod builds!!!
    )

    fun isQuarterlySurveyEnabled(): Boolean = isEnabled(
        debugEnabled = debugEnabled,
        debugFlag = debugFlags.isQuarterlySurveyEnabled,
        remoteFlag = configRepo.isQuarterlySurveyEnabled
    )
}

internal fun isEnabled(
    debugEnabled: Boolean,
    debugFlag: Boolean?,
    remoteFlag: Boolean
): Boolean = if (debugEnabled) debugFlag ?: remoteFlag else remoteFlag
