package uk.gov.govuk.dwp

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import uk.gov.govuk.dwp.data.BenefitsRepo

class BenefitsViewModelTest {
    private val benefitsRepo = mockk<BenefitsRepo>(relaxed = true)
    private lateinit var viewModel: BenefitsViewModel

    @Test
    fun `Init runs getBio`() = runTest {
        viewModel = BenefitsViewModel(benefitsRepo)

        coVerify(exactly = 1) {
            benefitsRepo.getCoreBio(any())
        }
    }

    @Test
    fun `Init runs getAwardDetails`() = runTest {
        viewModel = BenefitsViewModel(benefitsRepo)

        coVerify(exactly = 1) {
            benefitsRepo.getAwardDetails(any())
        }
    }

    @Test
    fun `Init runs getPaymentsQuery`() = runTest {
        viewModel = BenefitsViewModel(benefitsRepo)

        coVerify(exactly = 1) {
            benefitsRepo.getPaymentsQuery(any())
        }
    }
}
