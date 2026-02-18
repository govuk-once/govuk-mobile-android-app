package uk.gov.govuk.terms

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.remote.model.TermsAndConditions
import uk.gov.govuk.terms.data.TermsRepo
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TermsViewModelTest {

    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val termsRepo = mockk<TermsRepo>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given terms and conditions are present in the config, when init, then emit ui state`() {
        every { configRepo.termsAndConditions } returns TermsAndConditions(Date(), "termsUrl")

        val viewModel = TermsViewModel(configRepo, termsRepo)

        runTest {
            val uiState = viewModel.uiState.first()
            assertEquals("termsUrl", uiState?.termsUrl)
        }
    }

    @Test
    fun `When terms are accepted, then update repot and emit event`() {
        val viewModel = TermsViewModel(configRepo, termsRepo)

        runTest {
            val termsAccepted = mutableListOf<Unit>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.termsAccepted.toList(termsAccepted)
            }
            viewModel.onTermsAccepted()
            assertEquals(termsAccepted.size, 1)
        }

        coVerify {
            termsRepo.termsAccepted(any())
        }
    }

}