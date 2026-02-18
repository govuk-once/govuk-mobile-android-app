package uk.gov.govuk.terms.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.terms.data.local.TermsDataStore
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TermsRepoTest {

    private val termsDataStore = mockk<TermsDataStore>(relaxed = true)

    private lateinit var repo: TermsRepo

    @Before
    fun setup() {
        repo = TermsRepo(termsDataStore)
    }

    @Test
    fun `Given the there is no terms accepted date stored, When get terms accepted date, then return null`() {
        coEvery { termsDataStore.getTermsAcceptedDate() } returns null

        runTest {
            assertNull(repo.getTermsAcceptedDate())
        }
    }

    @Test
    fun `Given the there is a terms accepted date stored, When get terms accepted date, then return the date`() {
        coEvery { termsDataStore.getTermsAcceptedDate() } returns 123L

        runTest {
            assertEquals(123L, repo.getTermsAcceptedDate())
        }
    }

    @Test
    fun `When terms are accepted, then update the data store`() {
        runTest {
            termsDataStore.setTermsAcceptedDate(123L)
        }

        coVerify {
            termsDataStore.setTermsAcceptedDate(123L)
        }
    }

    @Test
    fun `Given the there is no terms accepted date stored, When should display terms, then return true`() {
        coEvery { termsDataStore.getTermsAcceptedDate() } returns null

        runTest {
            assertTrue(repo.getTermsAcceptanceState())
        }
    }

    @Test
    fun `Given the there a terms accepted date stored, When should display terms, then return false`() {
        coEvery { termsDataStore.getTermsAcceptedDate() } returns 123L

        runTest {
            assertFalse(repo.getTermsAcceptanceState())
        }
    }

    @Test
    fun `When clear, then clear the data store`() {
        runTest {
            repo.clear()
        }

        coVerify {
            termsDataStore.clear()
        }
    }
}