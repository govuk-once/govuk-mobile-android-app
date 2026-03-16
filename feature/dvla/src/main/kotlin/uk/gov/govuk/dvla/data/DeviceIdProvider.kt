package uk.gov.govuk.dvla.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class DeviceIdProvider @Inject constructor(
    @param:Named("dvla_prefs") private val dataStore: DataStore<Preferences>
) {
    // TODO for POC, remove when linkingId for DVLA is available
    companion object {
        private const val DEVICE_ID_KEY = "app_scoped_device_id"
    }

    suspend fun getDeviceId(): String {
        val prefKey = stringPreferencesKey(DEVICE_ID_KEY)

        // create a random UUID if one doesn't exist and use it forever as per docs
        return dataStore.data.firstOrNull()?.get(prefKey)
            ?: UUID.randomUUID().toString().also { newId ->
                dataStore.edit { it[prefKey] = newId }
            }
    }

}