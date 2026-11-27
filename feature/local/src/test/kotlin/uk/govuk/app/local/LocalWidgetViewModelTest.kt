package uk.govuk.app.local

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.LocalWidgetUiState.LocalAuthoritySelected
import uk.govuk.app.local.LocalWidgetUiState.NoLocalAuthority
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.LocalAuthority

@OptIn(ExperimentalCoroutinesApi::class)
class LocalWidgetViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val localRepo = mockk<LocalRepo>(relaxed = true)
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
    fun `Given a local authority has not been selected, when init, then emit no local authority`() {
        every { localRepo.localAuthority } returns flowOf(null)

        val viewModel = LocalWidgetViewModel(localRepo, analyticsClient)

        runTest {
            assertEquals(NoLocalAuthority, viewModel.uiState.value)
        }
    }

    @Test
    fun `Given a local authority has been selected, when init, then emit local authority`() {
        val localAuthority = LocalAuthority(
            name = "name",
            url = "url",
            slug = "slug",
            parent = LocalAuthority(
                name = "parentName",
                url = "parentUrl",
                slug = "parentSlug"
            )
        )

        every { localRepo.localAuthority } returns flowOf(localAuthority)

        val viewModel = LocalWidgetViewModel(localRepo, analyticsClient)

        runTest {
            assertEquals(LocalAuthoritySelected(localAuthority), viewModel.uiState.value)
        }
    }

    @Test
    fun `Given the user clicks edit, then send edit function event`() {
        every { localRepo.localAuthority } returns flowOf(null)

        val viewModel = LocalWidgetViewModel(localRepo, analyticsClient)
        viewModel.onEditClick("Edit")

        verify {
            analyticsClient.buttonFunction(
                text = "Edit",
                section = "Homescreen",
                action = "Edit"
            )
        }
    }

    @Test
    fun `Given the user clicks change, then send change button click event`() {
        every { localRepo.localAuthority } returns flowOf(null)

        val viewModel = LocalWidgetViewModel(localRepo, analyticsClient)
        viewModel.onChangeClick("Change")

        verify {
            analyticsClient.buttonClick(
                text = "Change",
                section = "Local"
            )
        }
    }

    @Test
    fun `Given the user confirms remove, then send remove function event and clear local authority`() {
        every { localRepo.localAuthority } returns flowOf(null)

        val viewModel = LocalWidgetViewModel(localRepo, analyticsClient)
        viewModel.onRemoveConfirmClick("Remove")

        verify {
            analyticsClient.buttonFunction(
                text = "Remove",
                section = "Local",
                action = "Remove"
            )
        }

        coVerify {
            localRepo.clear()
        }
    }
}
