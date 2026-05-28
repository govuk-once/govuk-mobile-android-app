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
import uk.gov.govuk.dvla.ui.model.Category
import uk.gov.govuk.dvla.data.local.DvlaDataStore.Companion.SELECTED_CATEGORY
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
    fun `Given the data store is empty, then getSelectedCategory returns null`() =
        runTest(dispatcher) {
            val appDatastore = DvlaDataStore(dataStore)

            assertNull(appDatastore.getSelectedCategory())
        }

    @Test
    fun `Given the selected category is vehicle, then getSelectedCategory returns vehicle`() =
        runTest(dispatcher) {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(SELECTED_CATEGORY)] = "VEHICLE"
            }

            val appDatastore = DvlaDataStore(dataStore)

            assertTrue(appDatastore.getSelectedCategory() == Category.VEHICLE)
        }

    @Test
    fun `Given the setSelectedCategory is called with vehicle, then the selected category in the data store is vehicle`() =
        runTest(dispatcher) {
            val appDatastore = DvlaDataStore(dataStore)

            appDatastore.setSelectedCategory(category = Category.VEHICLE)

            assertTrue(dataStore.data.first()[stringPreferencesKey(SELECTED_CATEGORY)] == "VEHICLE")
        }
}
