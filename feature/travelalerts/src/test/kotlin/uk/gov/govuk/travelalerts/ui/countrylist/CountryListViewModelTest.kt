package uk.gov.govuk.travelalerts.ui.countrylist

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.fixtures.TravelAlertsFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class CountryListViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private val notificationsRepo = mockk<NotificationsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private lateinit var viewModel: CountryListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = CountryListViewModel(travelAlertsRepo, notificationsRepo, analyticsClient)
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
        val unsorted = TravelAlertsFixtures.mockCountries.reversed() // Alphabetical in the fixture
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

        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Loaded)
    }

    @Test
    fun `Given loaded state, when retried, then state reloads`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Loaded)

        coEvery { travelAlertsRepo.getCountries() } returns Result.Error()
        viewModel.onRetry()

        assertTrue(viewModel.uiState.value is CountryListViewModel.State.Error)
    }

    @Test
    fun `Given loaded state, multiple page views do not trigger a reload`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()

        viewModel.onPageView()
        viewModel.onPageView()

        coVerify(exactly = 1) { travelAlertsRepo.getCountries() }
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

    // onCountrySelected — analytics

    @Test
    fun `Given country selected, then toggle analytics event is fired`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        val country = TravelAlertsFixtures.mockCountries.first()

        viewModel.onCountrySelected(country)

        verify {
            analyticsClient.toggleFunction(
                text = country.name,
                section = "Travel Abroad Notifications",
                action = "Add"
            )
        }
    }

    @Test
    fun `Given country selected with active search query, then search analytics event is fired`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        viewModel.onSearchQueryChange("fra")

        viewModel.onCountrySelected(TravelAlertsFixtures.mockCountries.first())

        verify { analyticsClient.search("fra", section = "country_search") }
    }

    @Test
    fun `Given country selected with no search query, then search analytics event is not fired`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()

        viewModel.onCountrySelected(TravelAlertsFixtures.mockCountries.first())

        verify(exactly = 0) { analyticsClient.search(any(), any()) }
    }

    // onSearchSubmitted — analytics

    @Test
    fun `Given search submitted with active query, then search analytics event is fired`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        viewModel.onSearchQueryChange("fra")

        viewModel.onSearchSubmitted()

        verify { analyticsClient.search("fra", section = "country_search") }
    }

    @Test
    fun `Given search submitted with empty query, then search analytics event is not fired`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()

        viewModel.onSearchSubmitted()

        verify(exactly = 0) { analyticsClient.search(any(), any()) }
    }

    // onCountrySelected — state

    @Test
    fun `Given country selected, then selected country is set in state`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        val country = TravelAlertsFixtures.mockCountries.first()

        viewModel.onCountrySelected(country)

        assertEquals(country, viewModel.selectedCountry.value)
    }

    // onDismissPreferenceSheet

    @Test
    fun `Given preference sheet shown, when dismissed, then selected country is null`() = runTest {
        viewModel.onDismissPreferenceSheet()

        assertEquals(null, viewModel.selectedCountry.value)
    }

    // onNotNowNotifications

    @Test
    fun `Given Not now tapped, when request succeeds, then navigation event is emitted`() = runTest {
        val events = mutableListOf<CountryListViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)
        viewModel.onNotNowNotifications(TravelAlertsFixtures.mockCountries.first())

        assertEquals(1, events.size)
        assertTrue(events.first() is CountryListViewModel.NavigationEvent.NavigateToTopic)
    }

    @Test
    fun `Given Not now tapped, when request succeeds, then sheet is closed`() = runTest {
        coEvery { travelAlertsRepo.followCountry(any(), any()) } returns Result.Success(Unit)

        viewModel.onNotNowNotifications(TravelAlertsFixtures.mockCountries.first())

        assertEquals(null, viewModel.selectedCountry.value)
    }

    @Test
    fun `Given Not now tapped, when request fails, then error flag is set`() = runTest {
        coEvery { travelAlertsRepo.followCountry(any(), any()) } returns Result.Error()

        viewModel.onNotNowNotifications(TravelAlertsFixtures.mockCountries.first())

        assertTrue(viewModel.followError.value)
    }

    // onGetNotificationsClick

    @Test
    fun `Given Get notifications tapped and permissions granted, when request succeeds, then navigation event is emitted`() = runTest {
        every { notificationsRepo.permissionGranted() } returns true
        val events = mutableListOf<CountryListViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)
        viewModel.onGetNotificationsClick(TravelAlertsFixtures.mockCountries.first())

        assertEquals(1, events.size)
        assertTrue(events.first() is CountryListViewModel.NavigationEvent.NavigateToTopic)
    }

    @Test
    fun `Given Get notifications tapped and permissions granted, when request fails, then error flag is set`() = runTest {
        every { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.followCountry(any(), any()) } returns Result.Error()

        viewModel.onGetNotificationsClick(TravelAlertsFixtures.mockCountries.first())

        assertTrue(viewModel.followError.value)
    }

    @Test
    fun `Given Get notifications tapped and permissions not granted, then navigates to rationale`() = runTest {
        every { notificationsRepo.permissionGranted() } returns false
        val events = mutableListOf<CountryListViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onGetNotificationsClick(TravelAlertsFixtures.mockCountries.first())

        assertEquals(1, events.size)
        assertTrue(events.first() is CountryListViewModel.NavigationEvent.NavigateToNotificationsRationale)
        assertEquals("france", (events.first() as CountryListViewModel.NavigationEvent.NavigateToNotificationsRationale).countrySlug)
    }

    // onSearchQueryChange

    @Test
    fun `Given search query, when query matches country name, then filtered results are returned`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()

        viewModel.onSearchQueryChange("fra")

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertEquals(listOf("France"), state.countries.map { it.name })
    }

    @Test
    fun `Given search query, when query is empty, then all countries are returned`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        viewModel.onSearchQueryChange("fra")

        viewModel.onSearchQueryChange("")

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertEquals(TravelAlertsFixtures.mockCountries.size, state.countries.size)
    }

    @Test
    fun `Given search query, when query is uppercase, then results are returned case-insensitively`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()

        viewModel.onSearchQueryChange("FRANCE")

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertEquals(listOf("France"), state.countries.map { it.name })
    }

    @Test
    fun `Given search query, when no countries match, then empty list is returned`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()

        viewModel.onSearchQueryChange("zzz")

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertTrue(state.countries.isEmpty())
    }

    @Test
    fun `Given search query, then query is preserved in state`() = runTest {
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()

        viewModel.onSearchQueryChange("fra")

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertEquals("fra", state.searchQuery)
    }

    @Test
    fun `Given search query, when query matches synonym, then country is returned`() = runTest {
        val countries = listOf(
            Country(name = "France", slug = "france", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = listOf("Gaul")),
            Country(name = "Germany", slug = "germany", rawLastUpdated = "2025-01-01T00:00:00Z", synonyms = emptyList())
        )
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(countries)
        viewModel.onPageView()

        viewModel.onSearchQueryChange("gaul")

        val state = viewModel.uiState.value as CountryListViewModel.State.Loaded
        assertEquals(listOf("France"), state.countries.map { it.name })
    }
}
