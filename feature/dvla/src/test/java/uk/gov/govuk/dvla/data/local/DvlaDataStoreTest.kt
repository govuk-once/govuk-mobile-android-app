package uk.gov.govuk.dvla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.data.local.DvlaDataStore.Companion.SELECTED_DRIVING_VIEW
import java.io.File
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCoroutinesApi::class)
class DvlaDataStoreTest {

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
    fun `Given the data store is empty, then getSelectedDrivingView returns null`() =
        runTest(dispatcher) {
            val dvlaDatastore = DvlaDataStore(dataStore)

            assertNull(dvlaDatastore.getSelectedDrivingView())
        }

    @Test
    fun `Given the selected driving view is vehicle, then getSelectedDrivingView returns vehicle`() =
        runTest(dispatcher) {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(SELECTED_DRIVING_VIEW)] = "VEHICLE"
            }

            val dvlaDatastore = DvlaDataStore(dataStore)

            assertTrue(dvlaDatastore.getSelectedDrivingView() == DrivingView.VEHICLE)
        }

    @Test
    fun `Given the setSelectedDrivingView is called with vehicle, then the selected driving view in the data store is vehicle`() =
        runTest(dispatcher) {
            val dvlaDatastore = DvlaDataStore(dataStore)

            dvlaDatastore.setSelectedDrivingView(drivingView = DrivingView.VEHICLE)

            assertTrue(dataStore.data.first()[stringPreferencesKey(SELECTED_DRIVING_VIEW)] == "VEHICLE")
        }

    @Test
    fun `Given the data store is cleared, when clear, then the data store is cleared`() =
        runTest(dispatcher) {
            val dvlaDatastore = DvlaDataStore(dataStore)

            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(SELECTED_DRIVING_VIEW)] = "VEHICLE"
            }

            assertTrue(dataStore.data.first().asMap().isNotEmpty())

            dvlaDatastore.clear()

            assertTrue(dataStore.data.first().asMap().isEmpty())
        }
}
