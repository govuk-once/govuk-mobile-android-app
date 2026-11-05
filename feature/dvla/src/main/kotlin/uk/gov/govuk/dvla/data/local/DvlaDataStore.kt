package uk.gov.govuk.dvla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import uk.gov.govuk.dvla.ui.model.DrivingView
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class DvlaDataStore @Inject constructor(
    @param:Named("dvla_prefs") private val dataStore: DataStore<Preferences>
) {
    internal companion object {
        const val SELECTED_DRIVING_VIEW = "selected_driving_view"
    }

    internal suspend fun getSelectedDrivingView(): DrivingView? {
        dataStore.data.firstOrNull()?.get(stringPreferencesKey(SELECTED_DRIVING_VIEW))
            ?.let { drivingView ->
                return runCatching { DrivingView.valueOf(drivingView) }.getOrDefault(null)
            }
        return null
    }

    internal suspend fun setSelectedDrivingView(drivingView: DrivingView) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(SELECTED_DRIVING_VIEW)] = drivingView.name
        }
    }

    internal suspend fun clear() {
        dataStore.edit { preferences -> preferences.clear() }
    }
}
