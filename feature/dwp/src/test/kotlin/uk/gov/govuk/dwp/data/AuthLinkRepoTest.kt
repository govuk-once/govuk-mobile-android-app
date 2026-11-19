package uk.gov.govuk.dwp.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dwp.data.local.AuthDataStore
import uk.gov.govuk.dwp.remote.AuthApi

class AuthLinkRepoTest {
    private val api = mockk<AuthApi>()
    private val authRepo = mockk<AuthRepo>()
    private val dataStore = mockk<AuthDataStore>(relaxed = true)
    private lateinit var repo: uk.gov.govuk.dwp.data.AuthLinkRepo

    @Before
    fun setup() {
        every { dataStore.linkState } returns flowOf(ServiceLinkStatus.UNLINKED)
        repo = AuthLinkRepo(api, authRepo, dataStore)
    }

    @Test
    fun `Given linking succeeds, then pass the token to DwpApi and persist linked status`() = runTest {
        coEvery { api.linkDwpIdentity("linking-token") } returns Response.success(204, Unit)
        assertTrue(repo.linkAccount("linking-token") is Result.Success)
        coVerify(exactly = 1) { api.linkDwpIdentity("linking-token") }
        coVerify(exactly = 1) { dataStore.markLinked() }
    }

    @Test
    fun `Given linking fails, then return error without persisting linked status`() = runTest {
        coEvery { api.linkDwpIdentity("linking-token") } throws java.io.IOException("Unavailable")
        assertTrue(repo.linkAccount("linking-token") is Result.Error)
        coVerify(exactly = 0) { dataStore.markLinked() }
    }

    @Test
    fun `Given linking is cancelled, then propagate cancellation without persisting linked status`() = runTest {
        coEvery { api.linkDwpIdentity("linking-token") } throws CancellationException()
        val result = runCatching { repo.linkAccount("linking-token") }
        assertTrue(result.exceptionOrNull() is CancellationException)
        coVerify(exactly = 0) { dataStore.markLinked() }
    }

    @Test
    fun `When cleared, then clear DWP local status`() = runTest {
        repo.clear()
        coVerify(exactly = 1) { dataStore.clear() }
    }

    @Test
    fun `Given unauthorized linking, when credentials refresh, then retry and persist success`() = runTest {
        coEvery { api.linkDwpIdentity("linking-token") } returnsMany listOf(
            Response.error(401, mockk(relaxed = true)),
            Response.success(204, Unit)
        )
        coEvery { authRepo.refreshTokens() } returns true
        assertTrue(repo.linkAccount("linking-token") is Result.Success)
        coVerify(exactly = 2) { api.linkDwpIdentity("linking-token") }
        coVerify(exactly = 1) { authRepo.refreshTokens() }
        coVerify(exactly = 1) { dataStore.markLinked() }
    }

    @Test
    fun `Given rejected credential refresh, then return AuthError without persisting linked status`() = runTest {
        coEvery { api.linkDwpIdentity("linking-token") } returns Response.error(403, mockk(relaxed = true))
        coEvery { authRepo.refreshTokens() } returns false
        assertTrue(repo.linkAccount("linking-token") is Result.AuthError)
        coVerify(exactly = 1) { api.linkDwpIdentity("linking-token") }
        coVerify(exactly = 1) { authRepo.refreshTokens() }
        coVerify(exactly = 0) { dataStore.markLinked() }
    }

    @Test
    fun `Given a successful API call but failed persistence, then return Error`() = runTest {
        coEvery { api.linkDwpIdentity("linking-token") } returns Response.success(204, Unit)
        coEvery { dataStore.markLinked() } throws java.io.IOException("Cannot write")
        assertTrue(repo.linkAccount("linking-token") is Result.Error)
        coVerify(exactly = 0) { authRepo.refreshTokens() }
    }
}
