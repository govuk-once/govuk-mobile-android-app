package uk.gov.govuk.topics.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.topics.BuildConfig
import uk.gov.govuk.topics.DefaultTopicsFeature
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.data.remote.TopicsApi
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class TopicsModule {

    @Provides
    @Singleton
    fun providesTopicsFeature(topicsRepo: TopicsRepo): TopicsFeature {
        return DefaultTopicsFeature(topicsRepo)
    }

    @Provides
    @Singleton
    fun providesTopicsApi(): TopicsApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.TOPICS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TopicsApi::class.java)
    }

    @Singleton
    @Provides
    @Named("topics_prefs")
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile("topics_preferences") }
        )
    }
}