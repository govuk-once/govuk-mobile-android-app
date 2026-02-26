package uk.gov.govuk.config.data.remote.source

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import uk.gov.govuk.config.BuildConfig
import uk.gov.govuk.config.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConfigDataSource @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) {

    private val remoteConfigSettings = remoteConfigSettings {
        fetchTimeoutInSeconds = 15L
        minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0L else 3600L
    }

    init {
        applyConfigSettings()
    }

    private fun applyConfigSettings() {
        firebaseRemoteConfig.apply {
            setConfigSettingsAsync(remoteConfigSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    /**
     * Wipe the activated remote values and restore config settings
     */
    suspend fun clearRemoteValues() {
        try {
            firebaseRemoteConfig.reset().await()
        } catch (_: Exception) {
            // do nothing
        }

        applyConfigSettings()
    }

    suspend fun fetch(): Boolean {
        return try {
            firebaseRemoteConfig.fetch().await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun activate(): Boolean {
        return try {
            firebaseRemoteConfig.activate().await()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun fetchAndActivate(): Boolean {
        return if (fetch()) {
            activate()
        } else {
            false
        }
    }
}