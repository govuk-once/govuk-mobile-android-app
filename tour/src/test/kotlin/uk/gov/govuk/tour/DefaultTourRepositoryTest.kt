package uk.gov.govuk.tour

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.tour.data.local.TourDataStore

class DefaultTourRepositoryTest {

    private val dataStore = mockk<TourDataStore>(relaxed = true)
    private lateinit var repository: DefaultTourRepository

    @Before
    fun setUp() {
        repository = DefaultTourRepository(dataStore)
    }

    @Test
    fun `isTourSeen returns false when tour has not been seen`() = runTest {
        every { dataStore.isTourSeen("chat") } returns flowOf(false)

        val result = mutableListOf<Boolean>()
        repository.isTourSeen("chat").collect { result.add(it) }

        assertFalse(result.first())
    }

    @Test
    fun `isTourSeen returns true when tour has been seen`() = runTest {
        every { dataStore.isTourSeen("chat") } returns flowOf(true)

        val result = mutableListOf<Boolean>()
        repository.isTourSeen("chat").collect { result.add(it) }

        assertTrue(result.first())
    }

    @Test
    fun `markTourSeen delegates to data store with correct id`() = runTest {
        repository.markTourSeen("chat")

        coVerify { dataStore.markTourSeen("chat") }
    }

    @Test
    fun `different tour ids are tracked independently`() = runTest {
        every { dataStore.isTourSeen("chat") } returns flowOf(true)
        every { dataStore.isTourSeen("dvla") } returns flowOf(false)

        val chatResults = mutableListOf<Boolean>()
        val dvlaResults = mutableListOf<Boolean>()
        repository.isTourSeen("chat").collect { chatResults.add(it) }
        repository.isTourSeen("dvla").collect { dvlaResults.add(it) }

        assertTrue(chatResults.first())
        assertFalse(dvlaResults.first())
    }
}
