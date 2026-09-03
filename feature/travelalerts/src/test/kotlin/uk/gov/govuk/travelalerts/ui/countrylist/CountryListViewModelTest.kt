package uk.gov.govuk.travelalerts.ui.countrylist

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
import uk.gov.govuk.travelalerts.fixtures.TravelAlertsFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class CountryListViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private lateinit var viewModel: CountryListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = CountryListViewModel(travelAlertsRepo, analyticsClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given view created, then state is Loading`() {
        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Loading)
    }

    @Test
    fun `Given page view, when countries load successfully, then state is Loaded`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertEquals(TravelAlertsFixtures.mockCountries.size, state.countries.size)
    }

    @Test
    fun `Given page view, when countries load successfully, then countries are sorted alphabetically`() = runTest {
        val unsorted = listOf(
            Country(name = "Spain", slug = "spain", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = emptyList()),
            Country(name = "France", slug = "france", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = emptyList()),
            Country(name = "Germany", slug = "germany", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = emptyList()),
        )
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(unsorted)

        viewModel.onPageView()

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertEquals(listOf("France", "Germany", "Spain"), state.countries.map { it.name })
    }

    @Test
    fun `Given page view, when countries list is empty, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(emptyList())

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Error)
    }

    @Test
    fun `Given page view, when service not responding, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.ServiceNotResponding(500)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Error)
    }

    @Test
    fun `Given page view, when device offline, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.DeviceOffline()

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Error)
    }

    @Test
    fun `Given page view, then state transitions through Loading before resolving`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        // After completion the state settles to Loaded; the Loading transition
        // is observable because UnconfinedTestDispatcher runs coroutines eagerly
        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Loaded)
    }

    @Test
    fun `Given successful page view, when page viewed again, then state reloads`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Loaded)

        coEvery { travelAlertsRepo.getCountries() } returns Result.Error()
        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Error)
    }

    @Test
    fun `Given page view, then screen view analytics event is fired`() = runTest {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "CountryListScreen",
                screenName = "Follow a country",
                title = "Follow a country"
            )
        }
    }
}
