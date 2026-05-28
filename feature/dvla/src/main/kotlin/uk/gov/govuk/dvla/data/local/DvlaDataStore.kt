package uk.gov.govuk.dvla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import uk.gov.govuk.dvla.ui.model.Category
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class DvlaDataStore @Inject constructor(
    @param:Named("dvla_prefs") private val dataStore: DataStore<Preferences>
) {
    internal companion object {
        const val SELECTED_CATEGORY = "selected_category"
    }

    suspend fun getSelectedCategory(): Category? {
        dataStore.data.firstOrNull()?.get(stringPreferencesKey(SELECTED_CATEGORY))
            ?.let { category ->
                return Category.valueOf(category)
            }
        return null
    }

    suspend fun setSelectedCategory(category: Category) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(SELECTED_CATEGORY)] = category.name
        }
    }
}
