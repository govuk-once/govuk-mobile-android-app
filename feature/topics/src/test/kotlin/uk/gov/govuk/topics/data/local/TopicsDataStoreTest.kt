package uk.gov.govuk.topics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.topics.TopicsCategory

class TopicsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given topics customised is null, then return false`() = runTest {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(TopicsDataStore.TOPICS_CUSTOMISED)] } returns null

        val datastore = TopicsDataStore(dataStore)
        assertFalse(datastore.isTopicsCustomised())
    }

    @Test
    fun `Given topics customised is false, then return false`() = runTest {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(TopicsDataStore.TOPICS_CUSTOMISED)] } returns false

        val datastore = TopicsDataStore(dataStore)

        assertFalse(datastore.isTopicsCustomised())
    }

    @Test
    fun `Given topics customised is true, then return true`() = runTest {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(TopicsDataStore.TOPICS_CUSTOMISED)] } returns true

        val datastore = TopicsDataStore(dataStore)

        assertTrue(datastore.isTopicsCustomised())
    }

    @Test
    fun `Given selected category is null, then emit Your Topics`() = runTest {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringPreferencesKey(TopicsDataStore.SELECTED_CATEGORY)] } returns null

        val datastore = TopicsDataStore(dataStore)

        val result = datastore.selectedCategoryFlow.first()

        assertEquals(TopicsCategory.YOUR, result)
    }

    @Test
    fun `Given selected category is All Topics, then emit All Topics`() = runTest {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringPreferencesKey(TopicsDataStore.SELECTED_CATEGORY)] } returns TopicsCategory.ALL.name

        val datastore = TopicsDataStore(dataStore)

        val result = datastore.selectedCategoryFlow.first()

        assertEquals(TopicsCategory.ALL, result)
    }

    @Test
    fun `Given selected category is an invalid string, then fallback to Your Topics`() = runTest {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringPreferencesKey(TopicsDataStore.SELECTED_CATEGORY)] } returns "GARBAGE"

        val datastore = TopicsDataStore(dataStore)

        val result = datastore.selectedCategoryFlow.first()

        assertEquals(TopicsCategory.YOUR, result)
    }
}