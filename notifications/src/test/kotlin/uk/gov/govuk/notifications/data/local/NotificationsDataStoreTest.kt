package uk.gov.govuk.notifications.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.notifications.data.local.NotificationsDataStore.Companion.NOTIFICATIONS_FIRST_PERMISSION_REQUEST_COMPLETED_KEY
import uk.gov.govuk.notifications.data.local.NotificationsDataStore.Companion.NOTIFICATIONS_ONBOARDING_COMPLETED_KEY
import java.io.File
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsDataStoreTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var tempDir: File
    private lateinit var dataStore: DataStore<Preferences>

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        tempDir = File(createTempDirectory().toString())
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        tempDir.deleteRecursively()
    }

    @Test
    fun `Given the data store is empty, then is notifications onboarding completed returns false`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            assertFalse(appDatastore.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given notifications onboarding completed is false, then return false`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(NOTIFICATIONS_ONBOARDING_COMPLETED_KEY)] = false
            }

            assertFalse(appDatastore.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given notifications onboarding completed is true, then return true`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(NOTIFICATIONS_ONBOARDING_COMPLETED_KEY)] = true
            }

            assertTrue(appDatastore.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user completes notifications onboarding, then update the prefs`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            appDatastore.notificationsOnboardingCompleted()

            assertTrue(dataStore.data.first()[booleanPreferencesKey(NOTIFICATIONS_ONBOARDING_COMPLETED_KEY)] == true)
        }
    }

    @Test
    fun `Given the data store is empty, then is first permission request completed returns false`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            assertFalse(appDatastore.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given first permission request completed is false, then return false`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(NOTIFICATIONS_FIRST_PERMISSION_REQUEST_COMPLETED_KEY)] = false
            }

            assertFalse(appDatastore.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given first permission request completed is true, then return true`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(NOTIFICATIONS_FIRST_PERMISSION_REQUEST_COMPLETED_KEY)] = true
            }

            assertTrue(appDatastore.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given first permission request is completed, then update the prefs`() {
        val appDatastore = NotificationsDataStore(dataStore)

        runTest {
            appDatastore.firstPermissionRequestCompleted()

            assertTrue(dataStore.data.first()[booleanPreferencesKey(NOTIFICATIONS_FIRST_PERMISSION_REQUEST_COMPLETED_KEY)] == true)
        }
    }
}
