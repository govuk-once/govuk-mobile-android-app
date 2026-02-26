package uk.gov.govuk.chat.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class ChatDataStoreTest {

    private lateinit var tempDir: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var chatDataStore: ChatDataStore

    @Before
    fun setup() {
        tempDir = File(createTempDirectory().toString())
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
        chatDataStore = ChatDataStore(dataStore)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `Returns null for conversation id if data store is empty`() = runTest {
        assertNull(chatDataStore.conversationId())
    }

    @Test
    fun `Saves and returns conversation id`() = runTest {
        assertNull(chatDataStore.conversationId())

        chatDataStore.saveConversationId("123")
        assertEquals("123", chatDataStore.conversationId())
    }

    @Test
    fun `Clears the conversation id`() = runTest {
        chatDataStore.saveConversationId("123")
        assertEquals("123", chatDataStore.conversationId())

        chatDataStore.clearConversation()

        assertNull(chatDataStore.conversationId())
    }

    @Test
    fun `Returns false for chat intro seen if data store is empty`() = runTest {
        assertFalse(chatDataStore.isChatIntroSeen())
    }

    @Test
    fun `Returns true for chat intro seen if data store value is true`() = runTest {
        chatDataStore.saveChatIntroSeen()
        assertTrue(chatDataStore.isChatIntroSeen())
    }

    @Test
    fun `Sets chat seen to true`() = runTest {
        assertFalse(chatDataStore.isChatIntroSeen())

        chatDataStore.saveChatIntroSeen()
        assertTrue(chatDataStore.isChatIntroSeen())
    }

    @Test
    fun `Clears everything`() = runTest {
        chatDataStore.saveConversationId("123")
        chatDataStore.saveChatIntroSeen()

        assertEquals("123", chatDataStore.conversationId())
        assertTrue(chatDataStore.isChatIntroSeen())

        chatDataStore.clear()

        assertNull(chatDataStore.conversationId())
        assertFalse(chatDataStore.isChatIntroSeen())
    }
}
