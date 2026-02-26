package uk.gov.govuk.topics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TopicsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given topics customised is null, then return false`() {
        val datastore = TopicsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(TopicsDataStore.TOPICS_CUSTOMISED)] } returns null

        runTest {
            assertFalse(datastore.isTopicsCustomised())
        }
    }

    @Test
    fun `Given topics customised is false, then return false`() {
        val datastore = TopicsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(TopicsDataStore.TOPICS_CUSTOMISED)] } returns false

        runTest {
            assertFalse(datastore.isTopicsCustomised())
        }
    }

    @Test
    fun `Given topics customised is true, then return true`() {
        val datastore = TopicsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(TopicsDataStore.TOPICS_CUSTOMISED)] } returns true

        runTest {
            assertTrue(datastore.isTopicsCustomised())
        }
    }

}