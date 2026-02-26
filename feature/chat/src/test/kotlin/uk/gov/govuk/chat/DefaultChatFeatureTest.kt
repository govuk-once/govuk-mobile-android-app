package uk.gov.govuk.chat

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.chat.data.ChatRepo

class DefaultChatFeatureTest {

    private val chatRepo = mockk<ChatRepo>(relaxed = true)
    private lateinit var feature: DefaultChatFeature

    @Before
    fun setup() {
        feature = DefaultChatFeature(chatRepo)
    }

    @Test
    fun `Clear clears the repo`() {
        runTest {
            feature.clear()

            coVerify { chatRepo.clear() }
        }
    }
}
