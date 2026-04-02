package uk.gov.govuk.dvla

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.data.model.Result

@OptIn(ExperimentalCoroutinesApi::class)
class DvlaLinkWidgetViewModelTest {

    private val dvlaRepo = mockk<DvlaRepo>()
    private lateinit var viewModel: DvlaLinkWidgetViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = DvlaLinkWidgetViewModel(dvlaRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When the viewModel is initialized, then initial state is CHECKING`() {
        assertEquals(DvlaLinkState.CHECKING, viewModel.dvlaState.value)
    }

    @Test
    fun `Given the account is linked, when checkStatus is called, then state updates to LINKED`() = runTest {
        coEvery { dvlaRepo.isAccountLinked() } returns Result.Success(true)

        viewModel.checkStatus()

        assertEquals(DvlaLinkState.LINKED, viewModel.dvlaState.value)
        coVerify(exactly = 1) { dvlaRepo.isAccountLinked() }
    }

    @Test
    fun `Given the account is not linked, when checkStatus is called, then state updates to UNLINKED`() = runTest {
        coEvery { dvlaRepo.isAccountLinked() } returns Result.Success(false)

        viewModel.checkStatus()

        assertEquals(DvlaLinkState.UNLINKED, viewModel.dvlaState.value)
        coVerify(exactly = 1) { dvlaRepo.isAccountLinked() }
    }

    @Test
    fun `Given repo returns error, when checkStatus is called, then state defaults to UNLINKED`() = runTest {
        coEvery { dvlaRepo.isAccountLinked() } returns Result.Error()

        viewModel.checkStatus()

        assertEquals(DvlaLinkState.UNLINKED, viewModel.dvlaState.value)
        coVerify(exactly = 1) { dvlaRepo.isAccountLinked() }
    }
}
