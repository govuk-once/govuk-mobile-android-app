package uk.gov.govuk.tour.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TourDataStore @Inject constructor(
    @param:Named("tour_prefs") private val dataStore: DataStore<Preferences>
) {

    internal fun isTourSeen(tourId: String): Flow<Boolean> = dataStore.data
        .map { it[booleanPreferencesKey("tour_${tourId}_seen")] == true }
        .distinctUntilChanged()

    internal suspend fun markTourSeen(tourId: String) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("tour_${tourId}_seen")] = true
        }
    }
}
