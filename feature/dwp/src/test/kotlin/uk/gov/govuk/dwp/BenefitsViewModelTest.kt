package uk.gov.govuk.dwp

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dwp.data.BenefitsRepo

@OptIn(ExperimentalCoroutinesApi::class)
class BenefitsViewModelTest {
    private val benefitsRepo = mockk<BenefitsRepo>(relaxed = true)
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Init runs getBio`() = runTest(dispatcher) {
        BenefitsViewModel(benefitsRepo)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            benefitsRepo.getCoreBio(any())
        }
    }

    @Test
    fun `Init runs getAwardDetails`() = runTest(dispatcher) {
        BenefitsViewModel(benefitsRepo)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            benefitsRepo.getAwardDetails(any())
        }
    }

    @Test
    fun `Init runs getPaymentsQuery`() = runTest(dispatcher) {
        BenefitsViewModel(benefitsRepo)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            benefitsRepo.getPaymentsQuery(any())
        }
    }
}
