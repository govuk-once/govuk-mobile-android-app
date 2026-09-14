package uk.gov.govuk.travelalerts.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group
import uk.gov.govuk.travelalerts.data.model.Subgroup
import uk.gov.govuk.travelalerts.data.model.SubscriptionRequest
import uk.gov.govuk.travelalerts.data.remote.GroupsApi
import uk.gov.govuk.travelalerts.data.remote.TravelApi
import uk.gov.govuk.travelalerts.fixtures.TravelAlertsFixtures
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TravelAlertsRepoTest {

    private val api = mockk<GroupsApi>(relaxed = true)
    private val travelApi = mockk<TravelApi>(relaxed = true)
    private val auth = mockk<AuthRepo>(relaxed = true)
    private var mockDateProvider = mockk<DateProvider>()

    private val mockGetGroupsResponse = mockk<Response<List<Group>>>(relaxed = true)
    private val mockCountriesResponse = mockk<Response<List<Country>>>(relaxed = true)

    private lateinit var travelAlertsRepo: TravelAlertsRepoImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        every { mockDateProvider.date } returns Instant.now()
        coEvery { mockCountriesResponse.isSuccessful } returns true
        coEvery { mockCountriesResponse.body() } returns TravelAlertsFixtures.mockCountries
        coEvery { travelApi.getCountries() } returns mockCountriesResponse
        travelAlertsRepo = TravelAlertsRepoImpl(api, travelApi, auth, mockDateProvider)
    }

    // Get Groups
    
    @Test
    fun `Get groups performs API call`() = runTest {
        travelAlertsRepo.getGroups()

        coVerify {
            api.getGroups()
        }
    }

    @Test
    fun `Get groups uses cache on second call`() = runTest {
        coEvery { api.getGroups() } returns mockGetGroupsResponse
        coEvery { mockGetGroupsResponse.isSuccessful } returns true
        coEvery { mockGetGroupsResponse.body() } returns TravelAlertsFixtures.mockGroups

        travelAlertsRepo.getGroups()
        travelAlertsRepo.getGroups()

        coVerify(exactly = 1) {
            api.getGroups()
        }
    }

    @Test
    fun `Get groups returns success when API succeeds`() = runTest {
        coEvery { api.getGroups() } returns mockGetGroupsResponse
        coEvery { mockGetGroupsResponse.isSuccessful } returns true
        coEvery { mockGetGroupsResponse.body() } returns TravelAlertsFixtures.mockGroups

        val result = travelAlertsRepo.getGroups()

        assertTrue(result is Result.Success)
        assertEquals(TravelAlertsFixtures.mockGroups, (result as Result.Success).value)
    }

    // Get Countries

    @Test
    fun `Get countries returns success`() = runTest {
        val result = travelAlertsRepo.getCountries()

        assertTrue(result is Result.Success)
        assertEquals(TravelAlertsFixtures.mockCountries, (result as Result.Success).value)
    }

    @Test
    fun `Get countries fetches fresh data when cache expires`() = runTest {
        travelAlertsRepo.getCountries()

        every { mockDateProvider.date } returns Instant.now().plus(301, ChronoUnit.SECONDS)

        val result = travelAlertsRepo.getCountries()

        assertTrue(result is Result.Success)
        coVerify(exactly = 2) { travelApi.getCountries() }
    }

    // Subscribe to Country

    @Test
    fun `Subscribe to country calls API with correct subscription request`() = runTest {
        travelAlertsRepo.subscribeToCountry("france")

        coVerify {
            api.subscribeToGroups(
                listOf(
                    SubscriptionRequest(
                        namespace = "travel",
                        group = "france",
                        subgroup = Subgroup.DAILY
                    )
                )
            )
        }
    }

    @Test
    fun `Subscribe to country returns success when API succeeds`() = runTest {
        val mockResponse = mockk<Response<Unit>>(relaxed = true)
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code() } returns 204
        coEvery { api.subscribeToGroups(any()) } returns mockResponse

        val result = travelAlertsRepo.subscribeToCountry("france")

        assertTrue(result is Result.Success)
    }

    @Test
    fun `Subscribe to country clears groups cache on success`() = runTest {
        coEvery { api.getGroups() } returns mockGetGroupsResponse
        coEvery { mockGetGroupsResponse.isSuccessful } returns true
        coEvery { mockGetGroupsResponse.body() } returns TravelAlertsFixtures.mockGroups

        val mockResponse = mockk<Response<Unit>>(relaxed = true)
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code() } returns 204
        coEvery { api.subscribeToGroups(any()) } returns mockResponse

        travelAlertsRepo.getGroups()
        travelAlertsRepo.subscribeToCountry("france")
        travelAlertsRepo.getGroups()

        coVerify(exactly = 2) { api.getGroups() }
    }

    @Test
    fun `Subscribe to country does not clear groups cache on failure`() = runTest {
        coEvery { api.getGroups() } returns mockGetGroupsResponse
        coEvery { mockGetGroupsResponse.isSuccessful } returns true
        coEvery { mockGetGroupsResponse.body() } returns TravelAlertsFixtures.mockGroups

        val failResponse = mockk<Response<Unit>>()
        every { failResponse.isSuccessful } returns false
        every { failResponse.code() } returns 500
        coEvery { api.subscribeToGroups(any()) } returns failResponse

        travelAlertsRepo.getGroups()
        travelAlertsRepo.subscribeToCountry("france")
        travelAlertsRepo.getGroups()

        coVerify(exactly = 1) { api.getGroups() }
    }
}
