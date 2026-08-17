package uk.gov.govuk.settings

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.settings.domain.LinkedAccountsRepo
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel

@OptIn(ExperimentalCoroutinesApi::class)
class YourAccountsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val linkedAccountsRepo = mockk<LinkedAccountsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given repository returns linked accounts, when initialised, then linkedAccounts emits the list`() = runTest(dispatcher) {
        val mockAccount = mockk<LinkedAccountUiModel>(relaxed = true)
        every { linkedAccountsRepo.getLinkedAccounts() } returns flowOf(listOf(mockAccount))

        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.linkedAccounts.collect {}
        }

        advanceUntilIdle()

        assertEquals(listOf(mockAccount), viewModel.linkedAccounts.value)
    }

    @Test
    fun `Given service name, when onRemoveIconClicked is called, then track button function`() = runTest {
        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)

        viewModel.onRemoveIconClicked("dvla")

        verify(exactly = 1) {
            analyticsClient.buttonFunction(
                text = "dvla unlink",
                section = "Settings",
                action = "Edit"
            )
        }
    }

    @Test
    fun `Given service name and button label, when onUnlinkCancelled is called, then track button click`() = runTest {
        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)

        viewModel.onUnlinkCancelled("dvla", "Cancel")

        verify(exactly = 1) {
            analyticsClient.buttonClick(
                text = "DVLA Cancel",
                external = false,
                section = "Settings"
            )
        }
    }

    @Test
    fun `Given service, when unlinkAccount is called, then repository unlink is called`() = runTest(dispatcher) {
        coEvery { linkedAccountsRepo.unlinkAccount(any()) } returns Result.Success(Unit)
        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)

        viewModel.unlinkAccount("dvla", "Confirm")
        advanceUntilIdle()

        coVerify(exactly = 1) { linkedAccountsRepo.unlinkAccount("dvla") }
    }

    @Test
    fun `Given successful unlink, when unlinkAccount is called, then state changes from Unlinking to Default`() = runTest(dispatcher) {
        coEvery { linkedAccountsRepo.unlinkAccount(any()) } returns Result.Success(Unit)
        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)

        val uiStates = mutableListOf<LinkedAccountsUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.accountsUiState.toList(uiStates)
        }

        viewModel.unlinkAccount("dvla", "Confirm")
        advanceUntilIdle()

        assertEquals(LinkedAccountsUiState.Default, uiStates[0])
        assertEquals(LinkedAccountsUiState.Unlinking, uiStates[1])
        assertEquals(LinkedAccountsUiState.Default, uiStates[2])
    }

    @Test
    fun `Given successful unlink, when unlinkAccount is called, then error event is not emitted`() = runTest(dispatcher) {
        coEvery { linkedAccountsRepo.unlinkAccount(any()) } returns Result.Success(Unit)
        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)

        val errorEvents = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.errorEvent.toList(errorEvents)
        }

        viewModel.unlinkAccount("dvla", "Confirm")
        advanceUntilIdle()

        assertTrue(errorEvents.isEmpty())
    }

    @Test
    fun `Given failed unlink, when unlinkAccount is called, then state changes from Unlinking to Default`() = runTest(dispatcher) {
        coEvery { linkedAccountsRepo.unlinkAccount(any()) } returns Result.Error()
        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)

        val uiStates = mutableListOf<LinkedAccountsUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.accountsUiState.toList(uiStates)
        }

        viewModel.unlinkAccount("dvla", "Confirm")
        advanceUntilIdle()

        assertEquals(LinkedAccountsUiState.Default, uiStates[0])
        assertEquals(LinkedAccountsUiState.Unlinking, uiStates[1])
        assertEquals(LinkedAccountsUiState.Default, uiStates[2])
    }

    @Test
    fun `Given failed unlink, when unlinkAccount is called, then error event is emitted`() = runTest(dispatcher) {
        coEvery { linkedAccountsRepo.unlinkAccount(any()) } returns Result.Error()
        val viewModel = YourAccountsViewModel(linkedAccountsRepo, analyticsClient)

        val errorEvents = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.errorEvent.toList(errorEvents)
        }

        viewModel.unlinkAccount("dvla", "Confirm")
        advanceUntilIdle()

        assertEquals(1, errorEvents.size)
    }
}