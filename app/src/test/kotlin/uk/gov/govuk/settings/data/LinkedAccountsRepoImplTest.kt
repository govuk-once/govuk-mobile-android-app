package uk.gov.govuk.settings.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo

class LinkedAccountsRepoImplTest {

    private val dvlaRepo = mockk<DvlaRepo>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)

    private lateinit var repo: LinkedAccountsRepoImpl

    @Before
    fun setup() {
        repo = LinkedAccountsRepoImpl(dvlaRepo, flagRepo)
    }

    @Test
    fun `Given DVLA is linked and flag is enabled, when getLinkedAccounts is called, then return list with DVLA account`() = runTest {
        every { dvlaRepo.isLinked } returns MutableStateFlow(true)
        every { flagRepo.isDvlaLinkEnabled() } returns true

        val accounts = repo.getLinkedAccounts().first()

        assertEquals(1, accounts.size)
        assertEquals("dvla", accounts[0].serviceName)
        assertEquals(uk.gov.govuk.dvla.R.string.dvla_account_title, accounts[0].displayTitleRes)
    }

    @Test
    fun `Given DVLA is linked but flag is disabled, when getLinkedAccounts is called, then return empty list`() = runTest {
        every { dvlaRepo.isLinked } returns MutableStateFlow(true)
        every { flagRepo.isDvlaLinkEnabled() } returns false

        val accounts = repo.getLinkedAccounts().first()

        assertTrue(accounts.isEmpty())
    }

    @Test
    fun `Given DVLA is not linked and flag is enabled, when getLinkedAccounts is called, then return empty list`() = runTest {
        every { dvlaRepo.isLinked } returns MutableStateFlow(false)
        every { flagRepo.isDvlaLinkEnabled() } returns true

        val accounts = repo.getLinkedAccounts().first()

        assertTrue(accounts.isEmpty())
    }

    @Test
    fun `Given service is dvla, when unlinkAccount is called, then call dvlaRepo to unlink`() = runTest {
        val expectedResult = Result.Success(Unit)
        coEvery { dvlaRepo.unlinkAccount() } returns expectedResult

        val result = repo.unlinkAccount("dvla")

        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { dvlaRepo.unlinkAccount() }
    }

    @Test
    fun `Given service is dvla and repo returns Error, when unlinkAccount is called, then return Error`() = runTest {
        val expectedResult: Result<Unit> = Result.Error()

        coEvery { dvlaRepo.unlinkAccount() } returns expectedResult

        val result = repo.unlinkAccount("dvla")

        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { dvlaRepo.unlinkAccount() }
    }
}