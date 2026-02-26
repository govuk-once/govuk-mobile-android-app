package uk.gov.govuk.login.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LoginDataStore @Inject constructor(
    @param:Named("login_prefs") private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val REFRESH_TOKEN_EXPIRY_KEY = "refresh_token_expiry"
        internal const val REFRESH_TOKEN_ISSUED_AT_DATE_KEY = "refresh_token_issued_at_date"
    }

    internal suspend fun getRefreshTokenExpiryDate(): Long? {
        return dataStore.data.firstOrNull()
            ?.get(longPreferencesKey(REFRESH_TOKEN_EXPIRY_KEY))
    }

    internal suspend fun getRefreshTokenIssuedAtDate(): Long? {
        return dataStore.data.firstOrNull()
            ?.get(longPreferencesKey(REFRESH_TOKEN_ISSUED_AT_DATE_KEY))
    }

    internal suspend fun setRefreshTokenIssuedAtDate(issuedAtDate: Long) {
        dataStore.edit { prefs ->
            prefs[longPreferencesKey(REFRESH_TOKEN_ISSUED_AT_DATE_KEY)] = issuedAtDate
        }
    }

    internal suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
