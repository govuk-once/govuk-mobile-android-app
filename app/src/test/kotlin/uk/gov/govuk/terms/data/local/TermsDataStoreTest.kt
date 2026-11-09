package uk.gov.govuk.terms.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class TermsDataStoreTest {

    private lateinit var tempDir: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var termsDataStore: TermsDataStore

    @Before
    fun setup() {
        tempDir = File(createTempDirectory().toString())
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
        termsDataStore = TermsDataStore(dataStore)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `Returns null for terms accepted date if data store is empty`() = runTest {
        assertNull(termsDataStore.getTermsAcceptedDate())
    }

    @Test
    fun `Saves and returns terms accepted date`() = runTest {
        assertNull(termsDataStore.getTermsAcceptedDate())

        termsDataStore.setTermsAcceptedDate(123L)
        assertEquals(123L, termsDataStore.getTermsAcceptedDate())
    }

    @Test
    fun `Clears the data store`() = runTest {
        termsDataStore.setTermsAcceptedDate(123L)
        assertEquals(123L, termsDataStore.getTermsAcceptedDate())

        termsDataStore.clear()

        assertNull(termsDataStore.getTermsAcceptedDate())
    }
}