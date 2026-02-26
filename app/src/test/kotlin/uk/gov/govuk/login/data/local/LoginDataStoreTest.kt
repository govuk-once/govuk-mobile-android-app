package uk.gov.govuk.login.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
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
import uk.gov.govuk.login.data.local.LoginDataStore.Companion.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.govuk.login.data.local.LoginDataStore.Companion.REFRESH_TOKEN_ISSUED_AT_DATE_KEY
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LoginDataStoreTest {

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
    fun `Given the data store is empty, When get refresh token expiry date, then return null`() {
        val loginDatastore = LoginDataStore(dataStore)

        runTest {
            assertNull(loginDatastore.getRefreshTokenExpiryDate())
        }
    }

    @Test
    fun `Given the refresh token expiry date is 12345 in the data store, When get refresh token expiry date, then return 12345`() {
        val loginDatastore = LoginDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(REFRESH_TOKEN_EXPIRY_KEY)] = 12345L
            }

            assertEquals(12345L, loginDatastore.getRefreshTokenExpiryDate())
        }
    }

    @Test
    fun `Given the refresh token issued at date is 12345 in the data store, When get refresh token issued at date, then return 12345`() {
        val loginDatastore = LoginDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(REFRESH_TOKEN_ISSUED_AT_DATE_KEY)] = 12345L
            }

            assertEquals(12345L, loginDatastore.getRefreshTokenIssuedAtDate())
        }
    }

    @Test
    fun `Given the refresh token issued at date is 12345, When set refresh token issued at date, then update the prefs`() {
        val loginDatastore = LoginDataStore(dataStore)

        runTest {
            loginDatastore.setRefreshTokenIssuedAtDate(12345L)

            assertTrue(
                dataStore.data.first()[longPreferencesKey(REFRESH_TOKEN_ISSUED_AT_DATE_KEY)] == 12345L
            )
        }
    }

    @Test
    fun `Given the data store is cleared, when clear, then the data store is cleared`() {
        val loginDatastore = LoginDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(REFRESH_TOKEN_EXPIRY_KEY)] = 12345L
            }

            dataStore.edit { prefs ->
                prefs[longPreferencesKey(REFRESH_TOKEN_ISSUED_AT_DATE_KEY)] = 12345L
            }

            assertTrue(dataStore.data.first().asMap().isNotEmpty())

            loginDatastore.clear()

            assertTrue(dataStore.data.first().asMap().isEmpty())
        }
    }
}
