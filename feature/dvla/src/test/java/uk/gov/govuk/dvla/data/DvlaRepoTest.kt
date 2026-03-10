package uk.gov.govuk.dvla.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.remote.DvlaApi

class DvlaRepoTest {

    private val api = mockk<DvlaApi>()
    private lateinit var repo: DvlaRepo
    private val linkingId = "linkingId"

    @Before
    fun setup() {
        repo = DvlaRepo(api)
    }

    @Test
    fun `Given linking api returns success, when linkAccount is called, then return Success`() = runTest {
        coEvery { api.linkDvlaIdentity(linkingId) } returns Response.success(Unit)

        val result = repo.linkAccount(linkingId)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.linkDvlaIdentity(linkingId) }
    }

    @Test
    fun `Given linking api throws exception, when linkAccount is called, then return Error`() = runTest {
        coEvery { api.linkDvlaIdentity(linkingId) } throws Exception("Exception")

        val result = repo.linkAccount(linkingId)

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.linkDvlaIdentity(linkingId) }
    }
}