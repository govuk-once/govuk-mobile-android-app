package uk.gov.govuk.notifications.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class NotificationsDataStore @Inject constructor(
    @param:Named("notification_prefs") private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val NOTIFICATIONS_ONBOARDING_COMPLETED_KEY = "notifications_onboarding_completed"
        internal const val NOTIFICATIONS_FIRST_PERMISSION_REQUEST_COMPLETED_KEY = "notifications_first_permission_request_completed"
    }

    internal suspend fun isNotificationsOnboardingCompleted(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(NOTIFICATIONS_ONBOARDING_COMPLETED_KEY)) == true
    }

    internal suspend fun notificationsOnboardingCompleted() {
        dataStore.edit { preferences -> preferences[booleanPreferencesKey(
            NOTIFICATIONS_ONBOARDING_COMPLETED_KEY
        )] = true
        }
    }

    internal suspend fun isFirstPermissionRequestCompleted(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(NOTIFICATIONS_FIRST_PERMISSION_REQUEST_COMPLETED_KEY)) == true
    }

    internal suspend fun firstPermissionRequestCompleted() {
        dataStore.edit { preferences -> preferences[booleanPreferencesKey(
            NOTIFICATIONS_FIRST_PERMISSION_REQUEST_COMPLETED_KEY
        )] = true
        }
    }
}
