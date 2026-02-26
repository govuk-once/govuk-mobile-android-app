package uk.gov.govuk.data.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import uk.gov.govuk.data.auth.local.TokenDataStore

class TokenRepoTest {

    private val tokenDataStore = mockk<TokenDataStore>(relaxed = true)

    @Test
    fun `Given we have a sub id, When get sub id, then return sub id`() {
        val repo = TokenRepo(tokenDataStore)

        coEvery { tokenDataStore.getSubId() } returns "12345"

        runTest {
            Assert.assertEquals("12345", repo.getSubId())
        }
    }

    @Test
    fun `Given we have a sub id, When save sub id, then save sub id is called`() {
        val repo = TokenRepo(tokenDataStore)

        runTest {
            repo.saveSubId("12345")

            coVerify { tokenDataStore.saveSubId("12345") }
        }
    }
}
