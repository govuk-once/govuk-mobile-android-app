package uk.gov.govuk.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.auth.local.TokenDataStore.Companion.SUB_ID_KEY
import java.io.File
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCoroutinesApi::class)
class TokenDataStoreTest {

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
    fun `Given the data store is empty, When get sub id, then return null`() {
        val tokenDataStore = TokenDataStore(dataStore)

        runTest {
            assertNull(tokenDataStore.getSubId())
        }
    }

    @Test
    fun `Given there is a sub id, When get sub id, then sub id is returned`() {
        val tokenDataStore = TokenDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(SUB_ID_KEY)] = "12345"
            }
            assertEquals("12345", tokenDataStore.getSubId())
        }
    }

    @Test
    fun `Given we have a sub id, When save sub id, then the data store has the sub id`() {
        val tokenDataStore = TokenDataStore(dataStore)

        runTest {
            tokenDataStore.saveSubId("12345")

            assertEquals("12345", tokenDataStore.getSubId())
        }
    }
}
