package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo

@OptIn(ExperimentalCoroutinesApi::class)
class DvlaViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val repo = mockk<DvlaRepo>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val token = "1234-abcd"
    private val dvlaAuthUrl = "https://gov.uk/dvla-auth-url"

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { savedStateHandle.get<String>("token") } returns token
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given account is not linked, when initialised, the initial state should be Loading`() = runTest(dispatcher) {
        every { repo.isLinked } returns false
        coEvery { repo.linkAccount(any()) } returns Result.Success(Unit)

        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        assertEquals(DvlaViewModel.UiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `Given account is not linked and linking api returns Success, when initialised, then emit LinkComplete event`() = runTest(dispatcher) {
        every { repo.isLinked } returns false
        coEvery { repo.linkAccount(any()) } returns Result.Success(Unit)

        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        val events = mutableListOf<DvlaViewModel.LinkingEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.linkingEvent.toList(events)
        }

        advanceUntilIdle()

        coVerify(exactly = 1) { repo.linkAccount(token) }
        assertEquals(DvlaViewModel.LinkingEvent.LinkComplete, events.first())
    }

    @Test
    fun `Given account is not linked and linking api returns Error, when initialised, then state should be Error`() = runTest(dispatcher) {
        every { repo.isLinked } returns false
        coEvery { repo.linkAccount(any()) } returns Result.Error()

        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        advanceUntilIdle()

        assertEquals(DvlaViewModel.UiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `Given no token in SavedStateHandle, when initialised, then start auth flow and set authUrlToLaunch`() = runTest(dispatcher) {
        every { savedStateHandle.get<String>("token") } returns null

        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        assertEquals(dvlaAuthUrl, viewModel.authUrlToLaunch.value)
        coVerify(exactly = 0) { repo.linkAccount(any()) }
    }

    @Test
    fun `Given auth url is set, when onAuthTabLaunched is called, then reset authUrlToLaunch to null`() = runTest(dispatcher) {
        every { savedStateHandle.get<String>("token") } returns null
        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        viewModel.onAuthTabLaunched()

        assertEquals(null, viewModel.authUrlToLaunch.value)
    }

    @Test
    fun `Given account is already linked, when initialised, the initial state should be Loading`() = runTest(dispatcher) {
        every { repo.isLinked } returns true
        coEvery { repo.unlinkAccount() } returns Result.Success(Unit)

        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        assertEquals(DvlaViewModel.UiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `Given account is already linked and unlink api returns Success, when initialised, then emit UnlinkComplete event`() = runTest(dispatcher) {
        every { repo.isLinked } returns true
        coEvery { repo.unlinkAccount() } returns Result.Success(Unit)

        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        val events = mutableListOf<DvlaViewModel.LinkingEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.linkingEvent.toList(events)
        }

        advanceUntilIdle()

        coVerify(exactly = 1) { repo.unlinkAccount() }
        assertEquals(DvlaViewModel.LinkingEvent.UnlinkComplete, events.first())
    }

    @Test
    fun `Given account is already linked and unlink api returns Error, when initialised, then state should be Error`() = runTest(dispatcher) {
        every { repo.isLinked } returns true
        coEvery { repo.unlinkAccount() } returns Result.Error()

        val viewModel = DvlaViewModel(savedStateHandle, repo, dvlaAuthUrl)

        advanceUntilIdle()

        assertEquals(DvlaViewModel.UiState.Error, viewModel.uiState.value)
    }
}

