package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
import uk.gov.govuk.travelalerts.fixtures.TravelAlertsFixtures
import uk.gov.govuk.travelalerts.navigation.SHOW_ERROR_ARG

@OptIn(ExperimentalCoroutinesApi::class)
class EditCountriesViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private lateinit var viewModel: EditCountriesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = EditCountriesViewModel(travelAlertsRepo, analyticsClient, SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given view created, then state is Loading`() {
        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Loading)
    }

    @Test
    fun `Given page view, when groups and countries load successfully, then state is Loaded`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(TravelAlertsFixtures.mockGroups.size, state.countries.size)
    }

    @Test
    fun `Given page view, when groups and countries load successfully, then only followed countries are displayed`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(listOf("France", "Germany", "Spain"), state.countries.map { it.name })
    }

    @Test
    fun `Given page view, when groups and countries load successfully, then countries are sorted alphabetically`() = runTest {
        val unsortedCountries = TravelAlertsFixtures.mockCountries.reversed() // Already alphabetical in fixture
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(unsortedCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(listOf("France", "Germany", "Spain"), state.countries.map { it.name })
    }

    @Test
    fun `Given page view, when groups is empty, then state is Loaded with no countries`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(emptyList())
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(0, state.countries.size)
    }

    @Test
    fun `Given page view, when groups loading fails, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Error()
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Error)
    }

    @Test
    fun `Given page view, when countries loading fails, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Error()

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Error)
    }

    @Test
    fun `Given page view, when groups service not responding, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.ServiceNotResponding(500)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Error)
    }

    @Test
    fun `Given page view, when countries service not responding, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.ServiceNotResponding(500)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Error)
    }

    @Test
    fun `Given page view, when device offline, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.DeviceOffline()
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Error)
    }

    @Test
    fun `Given page view, when country slug does not match any group, then country is filtered out`() = runTest {
        val groupsWithMismatchedSlug = listOf(
            TravelAlertsFixtures.mockGroups[0], // france
            TravelAlertsFixtures.mockGroups[1], // germany
            // spain group not included
        )
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(groupsWithMismatchedSlug)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(listOf("France", "Germany"), state.countries.map { it.name })
    }

    @Test
    fun `Given page view, then screen view analytics event is fired`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "EditCountriesScreen",
                screenName = "Edit countries",
                title = "Edit countries"
            )
        }
    }

    @Test
    fun `Given error state, when retried, then state reloads`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Error()
        coEvery { travelAlertsRepo.getCountries() } returns Result.Error()
        viewModel.onPageView()
        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Error)

        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onRetry()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(TravelAlertsFixtures.mockGroups.size, state.countries.size)
    }

    @Test
    fun `Given loaded state, when retried, then state reloads`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModel.onPageView()
        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Loaded)

        coEvery { travelAlertsRepo.getGroups() } returns Result.Error()
        coEvery { travelAlertsRepo.getCountries() } returns Result.Error()
        viewModel.onRetry()

        assertTrue(viewModel.uiState.value is EditCountriesViewModel.State.Error)
    }

    @Test
    fun `Given page view, then both groups and countries are fetched`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModel.onPageView()

        coVerify { travelAlertsRepo.getGroups() }
        coVerify { travelAlertsRepo.getCountries() }
    }

    @Test
    fun `Given page view, when all countries have been removed from backend, then state is Loaded with no countries`() = runTest {
        // Simulate backend removing all followed countries between widget load and edit page load
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(emptyList())

        viewModel.onPageView()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(0, state.countries.size)
    }

    @Test
    fun `Given toggle notifications succeeds, then isTogglingNotifications is false and toggleError is null`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.toggleNotifications("france", true) } returns Result.Success(Unit)
        viewModel.onPageView()

        viewModel.toggleNotifications("france", true)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertTrue(!state.isTogglingNotifications)
        assertEquals(null, state.toggleError)
    }

    @Test
    fun `Given toggle notifications fails, then isTogglingNotifications is false and toggleError is set`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.toggleNotifications("france", true) } returns Result.Error()
        viewModel.onPageView()

        viewModel.toggleNotifications("france", true)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertTrue(!state.isTogglingNotifications)
        assertEquals("Failed to update notifications", state.toggleError)
    }

    @Test
    fun `Given clear toggle error, then toggleError is null`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.toggleNotifications("france", true) } returns Result.Error()
        viewModel.onPageView()
        viewModel.toggleNotifications("france", true)

        viewModel.clearToggleError()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(null, state.toggleError)
    }

    @Test
    fun `Given unfollow country succeeds, then page reloads and isUnfollowing is false`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.unfollowCountry("france", true) } returns Result.Success(Unit)
        viewModel.onPageView()

        viewModel.unfollowCountry("france", true)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertTrue(!state.isUnfollowing)
        assertEquals(null, state.unfollowError)
    }

    @Test
    fun `Given unfollow country fails, then isUnfollowing is false and unfollowError is set`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.unfollowCountry("france", true) } returns Result.Error()
        viewModel.onPageView()

        viewModel.unfollowCountry("france", true)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertTrue(!state.isUnfollowing)
        assertEquals("Failed to unfollow country", state.unfollowError)
    }

    @Test
    fun `Given clear unfollow error, then unfollowError is null`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.unfollowCountry("france", true) } returns Result.Error()
        viewModel.onPageView()
        viewModel.unfollowCountry("france", true)

        viewModel.clearUnfollowError()

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(null, state.unfollowError)
    }

    @Test
    fun `Given showError nav argument, when page loads, then followError is set and flag is consumed`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf(SHOW_ERROR_ARG to true))
        val viewModelWithError = EditCountriesViewModel(travelAlertsRepo, analyticsClient, savedStateHandle)
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)

        viewModelWithError.onPageView()

        val state = viewModelWithError.uiState.value as EditCountriesViewModel.State.Loaded
        assertTrue(state.followError)
        assertEquals(false, savedStateHandle.get<Boolean>(SHOW_ERROR_ARG))
    }

    @Test
    fun `Given clear follow error, then followError is false`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf(SHOW_ERROR_ARG to true))
        val viewModelWithError = EditCountriesViewModel(travelAlertsRepo, analyticsClient, savedStateHandle)
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        viewModelWithError.onPageView()

        viewModelWithError.clearFollowError()

        val state = viewModelWithError.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals(false, state.followError)
    }

    @Test
    fun `Given toggle notifications succeeds when enabling, then groups subgroup for country is updated to daily`() = runTest {
        val groupsWithNotificationsOff = listOf(
            TravelAlertsFixtures.mockGroups[0].copy(subgroup = "none"), // france - notifications off
            TravelAlertsFixtures.mockGroups[1],                           // germany - notifications on
            TravelAlertsFixtures.mockGroups[2]                            // spain - notifications on
        )
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(groupsWithNotificationsOff)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.toggleNotifications("france", true) } returns Result.Success(Unit)
        viewModel.onPageView()

        viewModel.toggleNotifications("france", true)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals("daily", state.groups.find { it.group == "france" }?.subgroup)
    }

    @Test
    fun `Given toggle notifications succeeds when disabling, then groups subgroup for country is updated to none`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.toggleNotifications("france", false) } returns Result.Success(Unit)
        viewModel.onPageView()

        viewModel.toggleNotifications("france", false)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals("none", state.groups.find { it.group == "france" }?.subgroup)
    }

    @Test
    fun `Given toggle notifications succeeds, then only the toggled country's group is updated`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.toggleNotifications("france", false) } returns Result.Success(Unit)
        viewModel.onPageView()

        viewModel.toggleNotifications("france", false)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals("none", state.groups.find { it.group == "france" }?.subgroup)
        assertEquals("daily", state.groups.find { it.group == "germany" }?.subgroup)
        assertEquals("daily", state.groups.find { it.group == "spain" }?.subgroup)
    }

    @Test
    fun `Given toggle notifications fails, then groups subgroup is unchanged`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)
        coEvery { travelAlertsRepo.getCountries() } returns Result.Success(TravelAlertsFixtures.mockCountries)
        coEvery { travelAlertsRepo.toggleNotifications("france", false) } returns Result.Error()
        viewModel.onPageView()

        viewModel.toggleNotifications("france", false)

        val state = viewModel.uiState.value as EditCountriesViewModel.State.Loaded
        assertEquals("daily", state.groups.find { it.group == "france" }?.subgroup)
    }
}
