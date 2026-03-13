package uk.gov.govuk.dvla

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
import uk.gov.govuk.dvla.data.DeviceIdProvider
import uk.gov.govuk.dvla.data.DvlaRepo

@OptIn(ExperimentalCoroutinesApi::class)
class DvlaViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val repo = mockk<DvlaRepo>(relaxed = true)
    private val deviceIdProvider = mockk<DeviceIdProvider>(relaxed = true)
    private val linkingId = "linkingId"

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        every { deviceIdProvider.getDeviceId() } returns linkingId
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When initialised, the initial state should be Loading`() = runTest(dispatcher) {
        coEvery { repo.linkAccount(any()) } returns Result.Success(Unit)

        val viewModel = DvlaViewModel(repo, deviceIdProvider)

        assertEquals(DvlaViewModel.UiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `Given the linking api returns Success, when initialised, then emit LinkComplete event`() = runTest(dispatcher) {
        coEvery { repo.linkAccount(linkingId) } returns Result.Success(Unit)

        val viewModel = DvlaViewModel(repo, deviceIdProvider)

        val events = mutableListOf<DvlaViewModel.LinkingEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.linkingEvent.toList(events)
        }

        advanceUntilIdle()

        coVerify(exactly = 1) { repo.linkAccount(linkingId) }
        assertEquals(DvlaViewModel.LinkingEvent.LinkComplete, events.first())
    }

    @Test
    fun `Given the linking api returns Error, when initialised, then state should be Error`() = runTest(dispatcher) {
        coEvery { repo.linkAccount(linkingId) } returns Result.Error()

        val viewModel = DvlaViewModel(repo, deviceIdProvider)

        advanceUntilIdle()

        assertEquals(DvlaViewModel.UiState.Error, viewModel.uiState.value)
    }
}

