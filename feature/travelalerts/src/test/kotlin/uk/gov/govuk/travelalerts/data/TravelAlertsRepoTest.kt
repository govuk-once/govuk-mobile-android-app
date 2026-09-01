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
import uk.gov.govuk.travelalerts.data.model.Group
import uk.gov.govuk.travelalerts.data.remote.TravelAlertsApi
import uk.gov.govuk.travelalerts.fixtures.TravelAlertsFixtures
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TravelAlertsRepoTest {

    private val api = mockk<TravelAlertsApi>(relaxed = true)
    private val auth = mockk<AuthRepo>(relaxed = true)
    private var mockDateProvider = mockk<DateProvider>()

    private val mockGetGroupsResponse = mockk<Response<List<Group>>>(relaxed = true)

    private lateinit var travelAlertsRepo: TravelAlertsRepoImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        every { mockDateProvider.date } returns Instant.now()
        travelAlertsRepo = TravelAlertsRepoImpl(api, auth, mockDateProvider)
    }

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
}
