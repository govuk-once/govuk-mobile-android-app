package uk.gov.govuk.dwp

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.dwp.data.AuthLinkRepo

class AuthLinkWidgetViewModelTest {
    private val authLinkRepo = mockk<AuthLinkRepo>()

    @Test
    fun `Given the repository status, when initialized, then expose that status`() = runTest {
        every { this@AuthLinkWidgetViewModelTest.authLinkRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.UNLINKED)
        val viewModel = AuthLinkWidgetViewModel(this@AuthLinkWidgetViewModelTest.authLinkRepo)
        assertEquals(ServiceLinkStatus.UNLINKED, viewModel.dwpState.first())
    }

    @Test
    fun `Given the account is linked, when repository state changes, then expose the linked status`() = runTest {
        val state = MutableStateFlow(ServiceLinkStatus.UNLINKED)
        every { this@AuthLinkWidgetViewModelTest.authLinkRepo.linkState } returns state
        val viewModel = AuthLinkWidgetViewModel(this@AuthLinkWidgetViewModelTest.authLinkRepo)
        state.value = ServiceLinkStatus.LINKED
        assertEquals(ServiceLinkStatus.LINKED, viewModel.dwpState.first())
    }
}
