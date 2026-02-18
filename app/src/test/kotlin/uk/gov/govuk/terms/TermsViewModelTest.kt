package uk.gov.govuk.terms

import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.BuildConfig
import uk.gov.govuk.terms.data.TermsAcceptanceState
import uk.gov.govuk.terms.data.TermsRepo
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TermsViewModelTest {

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
    fun `Given terms acceptance state is new user, when init, then emit terms ui state with isUpdated false`() {
        coEvery { termsRepo.getTermsAcceptanceState() } returns TermsAcceptanceState.NewUser("https://terms.url")

        val viewModel = TermsViewModel(termsRepo)

        runTest {
            val uiState = viewModel.uiState.value as TermsUiState.Terms
            assertEquals("https://terms.url", uiState.termsUrl)
            assertEquals(BuildConfig.PRIVACY_POLICY_URL, uiState.privacyPolicyUrl)
            assertFalse(uiState.isUpdated)
        }
    }

    @Test
    fun `Given terms acceptance state is updated, when init, then emit terms ui state with isUpdated true`() {
        coEvery { termsRepo.getTermsAcceptanceState() } returns TermsAcceptanceState.Updated("https://terms.url")

        val viewModel = TermsViewModel(termsRepo)

        runTest {
            val uiState = viewModel.uiState.value as TermsUiState.Terms
            assertEquals("https://terms.url", uiState.termsUrl)
            assertEquals(BuildConfig.PRIVACY_POLICY_URL, uiState.privacyPolicyUrl)
            assertTrue(uiState.isUpdated)
        }
    }

    @Test
    fun `Given terms acceptance state is error, when init, then emit error ui state`() {
        coEvery { termsRepo.getTermsAcceptanceState() } returns TermsAcceptanceState.Error

        val viewModel = TermsViewModel(termsRepo)

        runTest {
            assertIs<TermsUiState.Error>(viewModel.uiState.value)
        }
    }

    @Test
    fun `Given terms acceptance state is accepted, when init, then terms accepted event is emitted`() = runTest {
        coEvery { termsRepo.getTermsAcceptanceState() } returns TermsAcceptanceState.Accepted

        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val viewModel = TermsViewModel(termsRepo)

        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.termsAccepted.toList(events)
        }

        advanceUntilIdle()

        assertEquals(1, events.size)
    }

    @Test
    fun `When terms are accepted, then update repo and emit event`() {
        val viewModel = TermsViewModel(termsRepo)

        runTest {
            val termsAccepted = mutableListOf<Unit>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.termsAccepted.toList(termsAccepted)
            }
            viewModel.onTermsAccepted()
            assertEquals(1, termsAccepted.size)
        }

        coVerify {
            termsRepo.termsAccepted(any())
        }
    }
}
