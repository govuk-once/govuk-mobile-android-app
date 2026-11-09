package uk.gov.govuk.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class AppDataStore @Inject constructor(
    @param:Named("app_prefs") private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val SKIPPED_BIOMETRICS_KEY = "skipped_biometrics"
        internal const val TOPIC_SELECTION_COMPLETED_KEY = "topic_selection_completed"
        internal const val SUPPRESSED_HOME_WIDGETS = "suppressed_home_widgets"
    }

    internal suspend fun hasSkippedBiometrics(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY)) == true
    }

    internal suspend fun skipBiometrics() {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY)] = true
        }
    }

    internal suspend fun clearBiometricsSkipped() {
        dataStore.edit { prefs ->
            prefs.remove(booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY))
        }
    }

    internal suspend fun isTopicSelectionCompleted(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY)) == true
    }

    internal suspend fun topicSelectionCompleted() {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY)] = true
        }
    }

    internal val suppressedHomeWidgets = dataStore.data
        .map { preferences ->
            preferences[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] ?: emptySet()
        }
        .distinctUntilChanged()

    internal suspend fun suppressHomeWidget(id: String) {
        val mutableWidgets = getSuppressedHomeWidgets()?.toMutableSet() ?: mutableSetOf()
        mutableWidgets.add(id)
        dataStore.edit { prefs -> prefs[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] = mutableWidgets.toSet() }
    }

    private suspend fun getSuppressedHomeWidgets() = dataStore.data.firstOrNull()
        ?.get(stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS))

    internal suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
