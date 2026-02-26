package uk.gov.govuk.analytics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.NOT_SET
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

enum class AnalyticsEnabledState {
    NOT_SET, ENABLED, DISABLED
}

@Singleton
class AnalyticsDataStore @Inject constructor(
    @param:Named("analytics_prefs") private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val ANALYTICS_ENABLED_KEY = "analytics_enabled"
    }

    private var _analyticsEnabledState: AnalyticsEnabledState
    val analyticsEnabledState: AnalyticsEnabledState
        get() = _analyticsEnabledState

    init {
        runBlocking {
            _analyticsEnabledState = dataStore.data.firstOrNull()?.get(booleanPreferencesKey(ANALYTICS_ENABLED_KEY))?.let { enabled ->
                if (enabled) ENABLED else DISABLED
            } ?: NOT_SET
        }
    }

    internal suspend fun analyticsEnabled() {
        _analyticsEnabledState = ENABLED
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(ANALYTICS_ENABLED_KEY)] = true
        }
    }

    internal suspend fun analyticsDisabled() {
        _analyticsEnabledState = DISABLED
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(ANALYTICS_ENABLED_KEY)] = false
        }
    }

    internal suspend fun clear() {
        _analyticsEnabledState = NOT_SET
        dataStore.edit { preferences ->
            preferences.remove(booleanPreferencesKey(ANALYTICS_ENABLED_KEY))
        }
    }
}