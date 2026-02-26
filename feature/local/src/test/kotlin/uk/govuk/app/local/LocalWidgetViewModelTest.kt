package uk.govuk.app.local

import io.mockk.every
import io.mockk.mockk
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
import uk.govuk.app.local.LocalWidgetUiState.LocalAuthoritySelected
import uk.govuk.app.local.LocalWidgetUiState.NoLocalAuthority
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.LocalAuthority

@OptIn(ExperimentalCoroutinesApi::class)
class LocalWidgetViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val localRepo = mockk<LocalRepo>(relaxed = true)

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

        val viewModel = LocalWidgetViewModel(localRepo)

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

        val viewModel = LocalWidgetViewModel(localRepo)

        runTest {
            assertEquals(LocalAuthoritySelected(localAuthority), viewModel.uiState.value)
        }
    }
}