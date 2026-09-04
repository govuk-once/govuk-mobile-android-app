package uk.gov.govuk.travelalerts.ui

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group
import uk.gov.govuk.travelalerts.fixtures.TravelAlertsFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class TravelAlertsWidgetViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private lateinit var viewModel: TravelAlertsWidgetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = TravelAlertsWidgetViewModel(travelAlertsRepo, analyticsClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given view created, then state is Loading`() {
        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Loading)
    }

    @Test
    fun `Given page view, when both repos succeed, then state is Loaded with matched rows`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as TravelAlertsWidgetViewModel.State.Loaded
        assertEquals(TravelAlertsFixtures.mockCountries.size, state.rows.size)
        assertEquals("France", state.rows[0].headline)
        assertEquals("https://www.gov.uk/foreign-travel-advice/france", state.rows[0].link)
    }

    @Test
    fun `Given page view, when both repos succeed, then rows are sorted by country name`() = runTest {
        val unsortedGroups = listOf(
            Group(namespace = "ns1", group = "spain", subgroup = "daily"),
            Group(namespace = "ns2", group = "france", subgroup = "daily"),
            Group(namespace = "ns3", group = "germany", subgroup = "daily"),
        )
        val unsortedCountries = listOf(
            Country(name = "Spain", slug = "spain", rawLastUpdated = "2024-01-01T00:00:00Z"),
            Country(name = "France", slug = "france", rawLastUpdated = "2024-01-01T00:00:00Z"),
            Country(name = "Germany", slug = "germany", rawLastUpdated = "2024-01-01T00:00:00Z"),
        )
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(unsortedGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(unsortedCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as TravelAlertsWidgetViewModel.State.Loaded
        assertEquals(listOf("France", "Germany", "Spain"), state.rows.map { it.headline })
    }

    @Test
    fun `Given page view, when groups is empty, then state is Empty`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(emptyList())
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Empty)
    }

    @Test
    fun `Given page view, when no countries match groups, then state is Empty`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(emptyList())

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Empty)
    }

    @Test
    fun `Given page view, when groups fails, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.ServiceNotResponding(500)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Error)
    }

    @Test
    fun `Given page view, when countries fails, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.ServiceNotResponding(500)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Error)
    }

    @Test
    fun `Given page view, when offline, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.DeviceOffline()
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Error)
    }

    @Test
    fun `Given row click, then analytics event is fired with correct parameters`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        val state = viewModel.uiState.value as TravelAlertsWidgetViewModel.State.Loaded

        viewModel.onRowClick(state.rows.first { it.headline == "France" })

        verify {
            analyticsClient.widgetClick(
                text = "France",
                url = "https://www.gov.uk/foreign-travel-advice/france",
                external = true,
                section = "Travel Abroad Notifications"
            )
        }
    }
}
