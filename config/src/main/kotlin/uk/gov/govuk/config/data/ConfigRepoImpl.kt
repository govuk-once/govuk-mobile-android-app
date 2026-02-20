package uk.gov.govuk.config.data

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import uk.gov.govuk.config.data.remote.model.ChatBanner
import uk.gov.govuk.config.data.remote.source.FirebaseConfigDataSource
import uk.gov.govuk.config.data.remote.source.GovUkConfigDataSource
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.UserFeedbackBanner
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepoImpl @Inject constructor(
    private val govUkDataSource: GovUkConfigDataSource,
    private val firebaseDataSource: FirebaseConfigDataSource
): ConfigRepo {

    companion object {
        private const val CHAT_POLL_INTERVAL_FALLBACK = 3.0
    }

    private var _config: Config? = null
    private val safeConfig: Config
        get() = checkNotNull(_config) { "You must init config successfully before use!!!" }
    override suspend fun initConfig(): Result<Unit> = coroutineScope {
        val firebaseConfigDeferred = async {
            firebaseDataSource.fetch()
        }

        val govUkConfigResult = govUkDataSource.fetchConfig()

        firebaseConfigDeferred.await()

        if (govUkConfigResult is Success) {
            _config = govUkConfigResult.value
            Success(Unit)
        } else {
            @Suppress("UNCHECKED_CAST") // we know it's not a success
            govUkConfigResult as Result<Unit>
        }
    }

    override suspend fun activateRemoteConfig() = firebaseDataSource.activate()

    override suspend fun refreshRemoteConfig() {
        firebaseDataSource.fetchAndActivate()
    }

    override val isAvailable: Boolean
        get() = safeConfig.available

    override val chatPollIntervalSeconds: Double
        get() = (safeConfig.chatPollIntervalSeconds?.takeIf { it > 0.0 } ?: CHAT_POLL_INTERVAL_FALLBACK)

    override val minimumVersion: String
        get() = safeConfig.minimumVersion

    override val recommendedVersion: String
        get() = safeConfig.recommendedVersion


    override val isSearchEnabled: Boolean
        get() = safeConfig.releaseFlags.search

    override val isRecentActivityEnabled: Boolean
        get() = safeConfig.releaseFlags.recentActivity

    override val isTopicsEnabled: Boolean
        get() = safeConfig.releaseFlags.topics

    override val isNotificationsEnabled: Boolean
        get() = safeConfig.releaseFlags.notifications

    override val isLocalServicesEnabled: Boolean
        get() = safeConfig.releaseFlags.localServices

    override val isExternalBrowserEnabled: Boolean
        get() = safeConfig.releaseFlags.externalBrowser

    override val chatUrls: ChatUrls
        get() = safeConfig.chatUrls

    override val userFeedbackBanner: UserFeedbackBanner?
        get() = safeConfig.userFeedbackBanner

    override val refreshTokenExpirySeconds: Long?
        get() = safeConfig.refreshTokenExpirySeconds

    override val emergencyBanners: List<EmergencyBanner>?
        get() = safeConfig.emergencyBanners

    override val chatBanner: ChatBanner?
        get() = safeConfig.chatBanner

    override suspend fun clearRemoteConfigValues() {
        firebaseDataSource.clearRemoteValues()
    }

}
