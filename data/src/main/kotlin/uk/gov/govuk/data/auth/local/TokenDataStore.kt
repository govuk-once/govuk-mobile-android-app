package uk.gov.govuk.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TokenDataStore @Inject constructor(
    @param:Named("token_prefs") private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val SUB_ID_KEY = "sub_id"
    }

    internal suspend fun getSubId(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(SUB_ID_KEY))
    }

    internal suspend fun saveSubId(subId: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(SUB_ID_KEY)] = subId
        }
    }
}
