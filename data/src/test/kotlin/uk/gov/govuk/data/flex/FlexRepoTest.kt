package uk.gov.govuk.data.flex

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.flex.remote.FlexApi

class FlexRepoTest {

    private val flexApi = mockk<FlexApi>(relaxed = true)

    private lateinit var flexRepo: FlexRepo

    @Before
    fun setup() {
        flexRepo = FlexRepo(flexApi)
    }

    @Test
    fun `Given get flex preferences is called, then get flex preferences is called on the api`() =
        runTest {

            flexRepo.getFlexPreferences()

            coVerify { flexApi.getFlexPreferences() }
        }
}
