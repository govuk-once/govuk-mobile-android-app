package uk.gov.govuk.dwp

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.dwp.data.DwpLinkRepo

class DwpLinkWidgetViewModelTest {
    private val dwpLinkRepo = mockk<DwpLinkRepo>()

    @Test
    fun `Given the repository status, when initialized, then expose that status`() = runTest {
        every { this@DwpLinkWidgetViewModelTest.dwpLinkRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.UNLINKED)
        val viewModel = DwpLinkWidgetViewModel(this@DwpLinkWidgetViewModelTest.dwpLinkRepo)
        assertEquals(ServiceLinkStatus.UNLINKED, viewModel.dwpState.first())
    }

    @Test
    fun `Given the account is linked, when repository state changes, then expose the linked status`() = runTest {
        val state = MutableStateFlow(ServiceLinkStatus.UNLINKED)
        every { this@DwpLinkWidgetViewModelTest.dwpLinkRepo.linkState } returns state
        val viewModel = DwpLinkWidgetViewModel(this@DwpLinkWidgetViewModelTest.dwpLinkRepo)
        state.value = ServiceLinkStatus.LINKED
        assertEquals(ServiceLinkStatus.LINKED, viewModel.dwpState.first())
    }
}
