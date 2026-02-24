package uk.gov.govuk.terms.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.remote.model.TermsAndConditions
import uk.gov.govuk.terms.data.local.TermsDataStore
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TermsRepoTest {

    private val termsDataStore = mockk<TermsDataStore>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)

    private lateinit var repo: TermsRepo

    @Before
    fun setup() {
        repo = TermsRepo(termsDataStore, configRepo)
    }

    @Test
    fun `Given there is no terms config, when get terms acceptance state, then return error`() = runTest {
        every { configRepo.termsAndConditions } returns null

        assertIs<TermsAcceptanceState.Error>(repo.getTermsAcceptanceState())
    }

    @Test
    fun `Given there is no terms accepted date, when get terms acceptance state, then return new user`() = runTest {
        every { configRepo.termsAndConditions } returns TermsAndConditions(
            lastUpdated = "2024-01-01T00:00:00Z",
            url = "https://terms.url"
        )
        coEvery { termsDataStore.getTermsAcceptedDate() } returns null

        val state = repo.getTermsAcceptanceState()

        assertIs<TermsAcceptanceState.NewUser>(state)
        assertEquals("https://terms.url", state.termsUrl)
    }

    @Test
    fun `Given terms were updated after they were accepted, when get terms acceptance state, then return updated`() = runTest {
        every { configRepo.termsAndConditions } returns TermsAndConditions(
            lastUpdated = "2024-06-01T00:00:00Z",
            url = "https://terms.url"
        )
        coEvery { termsDataStore.getTermsAcceptedDate() } returns Instant.parse("2024-01-01T00:00:00Z").toEpochMilli()

        val state = repo.getTermsAcceptanceState()

        assertIs<TermsAcceptanceState.Updated>(state)
        assertEquals("https://terms.url", state.termsUrl)
    }

    @Test
    fun `Given terms were accepted after they were last updated, when get terms acceptance state, then return accepted`() = runTest {
        every { configRepo.termsAndConditions } returns TermsAndConditions(
            lastUpdated = "2024-01-01T00:00:00Z",
            url = "https://terms.url"
        )
        coEvery { termsDataStore.getTermsAcceptedDate() } returns Instant.parse("2024-06-01T00:00:00Z").toEpochMilli()

        assertIs<TermsAcceptanceState.Accepted>(repo.getTermsAcceptanceState())
    }

    @Test
    fun `Given the terms last updated date is invalid, when get terms acceptance state, then return error`() = runTest {
        every { configRepo.termsAndConditions } returns TermsAndConditions(
            lastUpdated = "not-a-date",
            url = "https://terms.url"
        )
        coEvery { termsDataStore.getTermsAcceptedDate() } returns 123L

        assertIs<TermsAcceptanceState.Error>(repo.getTermsAcceptanceState())
    }

    @Test
    fun `When terms are accepted, then update the data store`() = runTest {
        repo.termsAccepted(123L)

        coVerify {
            termsDataStore.setTermsAcceptedDate(123L)
        }
    }

    @Test
    fun `When clear, then clear the data store`() = runTest {
        repo.clear()

        coVerify {
            termsDataStore.clear()
        }
    }
}
