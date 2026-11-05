package uk.gov.govuk.terms.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TermsDataStore @Inject constructor(
    @param:Named("terms_prefs") private val dataStore: DataStore<Preferences>
) {
    companion object {
        private const val TERMS_ACCEPTED_DATE_KEY = "terms_accepted_date"
    }

    internal suspend fun getTermsAcceptedDate(): Long? {
        return dataStore.data.firstOrNull()
            ?.get(longPreferencesKey(TERMS_ACCEPTED_DATE_KEY))
    }

    internal suspend fun setTermsAcceptedDate(acceptedDate: Long) {
        dataStore.edit { prefs ->
            prefs[longPreferencesKey(TERMS_ACCEPTED_DATE_KEY)] = acceptedDate
        }
    }

    internal suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
