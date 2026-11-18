package uk.gov.govuk.dwp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import uk.gov.govuk.data.identity.model.ServiceLinkStatus

@Singleton
class AuthDataStore @Inject constructor(
    @param:Named("dwp_prefs") private val dataStore: DataStore<Preferences>
) {
    private val linkedKey = booleanPreferencesKey("dwp_linked")

    internal val linkState = dataStore.data.map { preferences ->
        if (preferences[linkedKey] == true) ServiceLinkStatus.LINKED else ServiceLinkStatus.UNLINKED
    }

    internal suspend fun markLinked() {
        dataStore.edit { it[linkedKey] = true }
    }

    internal suspend fun clear() {
        dataStore.edit { it.remove(linkedKey) }
    }
}
