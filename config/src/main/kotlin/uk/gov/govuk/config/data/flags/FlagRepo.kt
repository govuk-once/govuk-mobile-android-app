package uk.gov.govuk.config.data.flags

import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.extensions.isVersionLessThan
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlagRepo @Inject constructor(
    private val debugEnabled: Boolean,
    private val debugFlags: DebugFlags,
    private val configRepo: ConfigRepo
) {
    fun isAppAvailable(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isAppAvailable,
            remoteFlag = configRepo.isAvailable
        )
    }

    fun isForcedUpdate(appVersion: String): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.minimumVersion?.let {
                appVersion.isVersionLessThan(it)
            },
            remoteFlag = appVersion.isVersionLessThan(configRepo.minimumVersion)
        )
    }

    fun isRecommendUpdate(appVersion: String): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.recommendedVersion?.let {
                appVersion.isVersionLessThan(it)
            },
            remoteFlag = appVersion.isVersionLessThan(configRepo.recommendedVersion)
        )
    }

    fun isSearchEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isSearchEnabled,
            remoteFlag = configRepo.isSearchEnabled
        )
    }

    fun isRecentActivityEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isRecentActivityEnabled,
            remoteFlag = configRepo.isRecentActivityEnabled
        )
    }

    fun isTopicsEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isTopicsEnabled,
            remoteFlag = configRepo.isTopicsEnabled
        )
    }

    fun isNotificationsEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isNotificationsEnabled,
            remoteFlag = configRepo.isNotificationsEnabled
        )
    }

    fun isLocalServicesEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isLocalServicesEnabled,
            remoteFlag = configRepo.isLocalServicesEnabled
        )
    }

    fun isExternalBrowserEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isExternalBrowserEnabled,
            remoteFlag = configRepo.isExternalBrowserEnabled
        )
    }

    fun isChatEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isChatEnabled,
            remoteFlag = false //  Not yet wired up to remote config, always off for prod builds!!!
        )
    }
}

internal fun isEnabled(
    debugEnabled: Boolean,
    debugFlag: Boolean?,
    remoteFlag: Boolean
): Boolean {
    return if (debugEnabled) debugFlag ?: remoteFlag else remoteFlag
}
