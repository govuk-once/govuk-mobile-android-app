package uk.gov.govuk.chat.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class ChatDataStore @Inject constructor(
    @param:Named("chat_prefs") private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val CHAT_INTRO_SEEN_KEY = "chat_intro_seen"
        internal const val CONVERSATION_ID_KEY = "conversation_id"
    }

    internal suspend fun isChatIntroSeen(): Boolean {
        return dataStore.data.firstOrNull()?.get(booleanPreferencesKey(CHAT_INTRO_SEEN_KEY)) == true
    }

    internal suspend fun saveChatIntroSeen() {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(CHAT_INTRO_SEEN_KEY)] = true
        }
    }

    internal suspend fun conversationId(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(CONVERSATION_ID_KEY))
    }

    internal suspend fun saveConversationId(id: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(CONVERSATION_ID_KEY)] = id
        }
    }

    internal suspend fun clearConversation() {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(CONVERSATION_ID_KEY))
        }
    }

    internal suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
