package uk.gov.govuk.topics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import uk.gov.govuk.topics.TopicsCategory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TopicsDataStore @Inject constructor(
    @param:Named("topics_prefs") private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val TOPICS_CUSTOMISED = "topics_customised"
        internal const val SELECTED_CATEGORY = "selected_category"
    }

    internal suspend fun isTopicsCustomised(): Boolean {
        return dataStore.data.firstOrNull()?.get(booleanPreferencesKey(TOPICS_CUSTOMISED)) == true
    }

    internal suspend fun topicsCustomised() {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(TOPICS_CUSTOMISED)] = true
        }
    }

    internal val selectedCategoryFlow: Flow<TopicsCategory> = dataStore.data.map { preferences ->
        val savedValue = preferences[stringPreferencesKey(SELECTED_CATEGORY)] ?: TopicsCategory.YOUR.name
        runCatching { TopicsCategory.valueOf(savedValue) }.getOrDefault(TopicsCategory.YOUR)
    }

    internal suspend fun setSelectedCategory(category: TopicsCategory) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(SELECTED_CATEGORY)] = category.name
        }
    }

    internal suspend fun clear() {
       dataStore.edit { preferences -> preferences.clear() }
    }
}