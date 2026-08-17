package uk.gov.govuk.dvla

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.data.model.Result

@OptIn(ExperimentalCoroutinesApi::class)
class DvlaLinkWidgetViewModelTest {

    private val dvlaRepo = mockk<DvlaRepo>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private lateinit var viewModel: DvlaLinkWidgetViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.CHECKING)
        viewModel = DvlaLinkWidgetViewModel(dvlaRepo, analyticsClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When the viewModel is initialized, then state matches the repository's`() = runTest {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.CHECKING)
        val viewModel = DvlaLinkWidgetViewModel(dvlaRepo, analyticsClient)

        assertEquals(ServiceLinkStatus.CHECKING, viewModel.dvlaState.first())
    }

    @Test
    fun `Given state is UNLINKED, when checkStatus is called, then repository is called`() = runTest {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.UNLINKED)
        every { dvlaRepo.currentLinkState } returns ServiceLinkStatus.UNLINKED
        coEvery { dvlaRepo.refreshLinkStatus() } returns Unit

        val viewModel = DvlaLinkWidgetViewModel(dvlaRepo, analyticsClient)
        viewModel.checkStatus()

        advanceUntilIdle()

        coVerify(exactly = 1) { dvlaRepo.refreshLinkStatus() }
    }

    @Test
    fun `Given state is CHECKING, when checkStatus is called, then repository is called`() = runTest {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.CHECKING)
        every { dvlaRepo.currentLinkState } returns ServiceLinkStatus.CHECKING
        coEvery { dvlaRepo.refreshLinkStatus() } returns Unit

        val viewModel = DvlaLinkWidgetViewModel(dvlaRepo, analyticsClient)
        viewModel.checkStatus()

        advanceUntilIdle()

        coVerify(exactly = 1) { dvlaRepo.refreshLinkStatus() }
    }

    @Test
    fun `Given state is already LINKED, when checkStatus is called, then return without calling repository`() = runTest {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
        every { dvlaRepo.currentLinkState } returns ServiceLinkStatus.LINKED
        val viewModel = DvlaLinkWidgetViewModel(dvlaRepo, analyticsClient)

        viewModel.checkStatus()

        coVerify(exactly = 0) { dvlaRepo.refreshLinkStatus() }
    }

    @Test
    fun `Given a card click, when onLinkCardClicked is called, then track card click event`() {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.UNLINKED)
        val viewModel = DvlaLinkWidgetViewModel(dvlaRepo, analyticsClient)

        val expectedText = "Link DVLA account"

        viewModel.onLinkCardClicked(expectedText)

        verify(exactly = 1) {
            analyticsClient.cardClick(
                text = expectedText,
                external = false,
                section = "account link"
            )
        }
    }
}
