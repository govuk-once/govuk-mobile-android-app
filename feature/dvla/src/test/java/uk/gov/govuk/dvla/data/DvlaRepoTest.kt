package uk.gov.govuk.dvla.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.dvla.remote.model.DvlaStatusResponse

class DvlaRepoTest {

    private val api = mockk<DvlaApi>()
    private val authRepo = mockk<AuthRepo>()
    private lateinit var repo: DvlaRepo
    private val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJleHAiOjM2MDAsImxpbmtpbmdfaWQiOiIxMjM0LWFiY2QifQ."
    private val linkingId = "1234-abcd"

    @Before
    fun setup() {
        repo = DvlaRepo(api, authRepo)
    }

    @Test
    fun `Given linking api returns success, when linkAccount is called, then return Success`() = runTest {
        coEvery { api.linkDvlaIdentity(linkingId) } returns Response.success(Unit)

        val result = repo.linkAccount(token)

        assertTrue(result is Result.Success)
        assertTrue(repo.isLinked.value)
        coVerify(exactly = 1) { api.linkDvlaIdentity(linkingId) }
    }

    @Test
    fun `Given linking api throws exception, when linkAccount is called, then return Error`() = runTest {
        coEvery { api.linkDvlaIdentity(linkingId) } throws Exception("Exception")

        val result = repo.linkAccount(token)

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.linkDvlaIdentity(linkingId) }
    }

    @Test
    fun `Given unlinking api returns success, when unlinkAccount is called, then return Success and update isLinked`() = runTest {
        coEvery { api.linkDvlaIdentity(linkingId) } returns Response.success(Unit)
        repo.linkAccount(token)
        assertTrue(repo.isLinked.value)

        coEvery { api.deleteDvlaIdentity() } returns Response.success(Unit)
        val result = repo.unlinkAccount()

        assertTrue(result is Result.Success)
        assertFalse(repo.isLinked.value)
        coVerify(exactly = 1) { api.deleteDvlaIdentity() }
    }

    @Test
    fun `Given unlinking api throws exception, when unlinkAccount is called, then return error`() = runTest {
        coEvery { api.linkDvlaIdentity(linkingId) } returns Response.success(Unit)
        repo.linkAccount(token)
        assertTrue(repo.isLinked.value)

        coEvery { api.deleteDvlaIdentity() } throws Exception("Exception")
        val result = repo.unlinkAccount()

        assertTrue(result is Result.Error)
        assertTrue(repo.isLinked.value)
        coVerify(exactly = 1) { api.deleteDvlaIdentity() }
    }

    @Test
    fun `Given check api returns account is linked, when isAccountLinked is called, then return Success and update isLinked`() = runTest {
        coEvery { api.checkDvlaLinked() } returns Response.success(DvlaStatusResponse(linked = true))

        val result = repo.isAccountLinked()

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).value)
        assertTrue(repo.isLinked.value)
        coVerify(exactly = 1) { api.checkDvlaLinked() }
    }

    @Test
    fun `Given check api returns linked false, when isAccountLinked is called, then return Success(false) and update isLinked`() = runTest {
        coEvery { api.checkDvlaLinked() } returns Response.success(DvlaStatusResponse(linked = false))

        val result = repo.isAccountLinked()

        assertTrue(result is Result.Success)
        assertFalse((result as Result.Success).value)
        assertFalse(repo.isLinked.value)
        coVerify(exactly = 1) { api.checkDvlaLinked() }
    }

    @Test
    fun `Given check api throws exception, when isAccountLinked is called, then return Error and do not update isLinked`() = runTest {
        coEvery { api.checkDvlaLinked() } throws Exception("Exception")

        val result = repo.isAccountLinked()

        assertTrue(result is Result.Error)
        assertFalse(repo.isLinked.value)
        coVerify(exactly = 1) { api.checkDvlaLinked() }
    }
}
