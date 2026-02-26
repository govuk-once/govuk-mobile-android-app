package uk.gov.govuk.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.local.AppDataStore.Companion.SKIPPED_BIOMETRICS_KEY
import uk.gov.govuk.data.local.AppDataStore.Companion.SUPPRESSED_HOME_WIDGETS
import uk.gov.govuk.data.local.AppDataStore.Companion.TOPIC_SELECTION_COMPLETED_KEY
import java.io.File
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCoroutinesApi::class)
class AppDataStoreTest {

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
    fun `Given the data store is empty, When has skipped biometrics, then return false`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            assertFalse(appDatastore.hasSkippedBiometrics())
        }
    }

    @Test
    fun `Given the skipped biometrics flag is false in the data store, When has skipped biometrics, then return false`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY)] = false
            }

            assertFalse(appDatastore.hasSkippedBiometrics())
        }
    }

    @Test
    fun `Given the skipped biometrics flag is true in the data store, When has skipped biometrics, then return true`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY)] = true
            }

            assertTrue(appDatastore.hasSkippedBiometrics())
        }
    }

    @Test
    fun `Given the user skips biometrics, When skip biometrics, then update the prefs`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            assertFalse(appDatastore.hasSkippedBiometrics())

            appDatastore.skipBiometrics()

            assertTrue(dataStore.data.first().get(booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY)) == true)
        }
    }

    @Test
    fun `Given skip biometrics is cleared, then update the prefs`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            appDatastore.skipBiometrics()

            assertTrue(appDatastore.hasSkippedBiometrics())

            appDatastore.clearBiometricsSkipped()

            assertFalse(appDatastore.hasSkippedBiometrics())
            assertFalse(dataStore.data.first().contains(booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY)))
        }
    }

    @Test
    fun `Given the data store is empty, When is topic selection completed, then return false`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            assertFalse(appDatastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the topic selection completed flag is false in the data store, When is topic selection completed, then return false`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY)] = false
            }

            assertFalse(appDatastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the topic selection completed flag is true in the data store, When is topic selection completed, then return true`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY)] = true
            }

            assertTrue(appDatastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the data store is empty, when collecting suppressed home widgets, then flow is empty`() {
        val appDatastore = AppDataStore(dataStore)
        
        runTest {
            assertTrue(appDatastore.suppressedHomeWidgets.first().isEmpty())
        }
    }

    @Test
    fun `Given the suppressed widgets are empty, when collecting suppressed home widgets, then flow emits an empty set`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] = emptySet()
            }

            assertTrue(appDatastore.suppressedHomeWidgets.first().isEmpty())
        }
    }

    @Test
    fun `Given the suppressed widgets are present, when collecting suppressed home widgets, then flow emits a set containing suppressed widgets`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] = setOf("Widget 1", "Widget 2")
            }

            val suppressedWidgets = appDatastore.suppressedHomeWidgets.first()
            assertEquals(2, suppressedWidgets.size)
            assertTrue(suppressedWidgets.contains("Widget 1"))
            assertTrue(suppressedWidgets.contains("Widget 2"))
        }
    }

    @Test
    fun `Given the data store is cleared, when clear, then the data store is cleared`() {
        val appDatastore = AppDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(SKIPPED_BIOMETRICS_KEY)] = true
                prefs[booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY)] = true
                prefs[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] =
                    setOf("Widget 1", "Widget 2")
            }

            assertTrue(dataStore.data.first().asMap().isNotEmpty())

            appDatastore.clear()

            assertTrue(dataStore.data.first().asMap().isEmpty())
        }
    }
}