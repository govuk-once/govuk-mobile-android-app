package uk.gov.govuk.dwp.data

import io.mockk.coVerify
import io.mockk.mockk
import java.security.SecureRandom
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dwp.data.remote.PaymentsApi
import uk.gov.govuk.dwp.data.remote.PersonalApi

class BenefitsRepoTest {
    private val paymentsApi = mockk<PaymentsApi>(relaxed = true)
    private val personalApi = mockk<PersonalApi>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private lateinit var benefitsRepo: BenefitsRepo

    @Before
    fun setup() {
        benefitsRepo = BenefitsRepo(paymentsApi, personalApi, authRepo)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Get bio performs API call when there is a GUID`() = runTest {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        val guid = bytes.toHexString()

        benefitsRepo.getCoreBio(guid)

        coVerify(exactly = 1) {
            personalApi.getCoreBio(guid)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Get award details performs API call when there is a GUID`() = runTest {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        val guid = bytes.toHexString()

        benefitsRepo.getAwardDetails(guid)

        coVerify(exactly = 1) {
            paymentsApi.getAwardDetails(guid)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Get payments query performs API call when there is a GUID`() = runTest {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        val guid = bytes.toHexString()

        benefitsRepo.getPaymentsQuery(guid)

        coVerify(exactly = 1) {
            paymentsApi.getPaymentsQuery(guid)
        }
    }
}
