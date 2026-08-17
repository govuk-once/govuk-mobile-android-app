package uk.gov.govuk.data.identity

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.identity.model.IdentityState
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.data.identity.remote.IdentityApi
import uk.gov.govuk.data.identity.remote.IdentityResponse


class IdentityRepoTest {

    private val api = mockk<IdentityApi>()
    private val authRepo = mockk<AuthRepo>()

    private lateinit var repo: IdentityRepo

    @Before
    fun setup() {
        repo = IdentityRepo(api, authRepo)
    }

    @Test
    fun `Given repo is initialized, then initial state is Checking`() = runTest {
        assertEquals(IdentityState.Checking, repo.state.value)
        assertEquals(ServiceLinkStatus.CHECKING, repo.currentStatusOf(LinkedService.DVLA))
        assertEquals(ServiceLinkStatus.CHECKING, repo.linkStatusOf(LinkedService.DVLA).first())
    }

    @Test
    fun `Given api returns success with dvla, when getLinkedServices is called, then state is Success and status is LINKED`() = runTest {
        val mockResponse = mockk<IdentityResponse>(relaxed = true)
        every { mockResponse.services } returns listOf("dvla")
        coEvery { api.getLinkedServices() } returns Response.success(mockResponse)

        repo.getLinkedServices()

        val state = repo.state.value
        assertTrue(state is IdentityState.Success)
        assertTrue((state as IdentityState.Success).services.contains(LinkedService.DVLA))

        assertEquals(ServiceLinkStatus.LINKED, repo.currentStatusOf(LinkedService.DVLA))
        assertEquals(ServiceLinkStatus.LINKED, repo.linkStatusOf(LinkedService.DVLA).first())

        coVerify(exactly = 1) { api.getLinkedServices() }
    }

    @Test
    fun `Given api returns success without dvla, when getLinkedServices is called, then status is UNLINKED`() = runTest {
        val mockResponse = mockk<IdentityResponse>(relaxed = true)
        every { mockResponse.services } returns listOf("other service")
        coEvery { api.getLinkedServices() } returns Response.success(mockResponse)

        repo.getLinkedServices()

        assertEquals(ServiceLinkStatus.UNLINKED, repo.currentStatusOf(LinkedService.DVLA))
        assertEquals(ServiceLinkStatus.UNLINKED, repo.linkStatusOf(LinkedService.DVLA).first())
    }

    @Test
    fun `Given api throws exception, when getLinkedServices is called, then state is Error and status is ERROR`() = runTest {
        coEvery { api.getLinkedServices() } throws Exception("Exception")

        repo.getLinkedServices()

        assertEquals(IdentityState.Error, repo.state.value)
        assertEquals(ServiceLinkStatus.UNLINKED, repo.currentStatusOf(LinkedService.DVLA))
        assertEquals(ServiceLinkStatus.ERROR, repo.linkStatusOf(LinkedService.DVLA).first())
    }
}