package uk.gov.govuk.dvla.di

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
import uk.gov.govuk.dvla.BuildConfig
import uk.gov.govuk.dvla.remote.DvlaApi

import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DvlaModule {

    @Provides
    @Singleton
    fun providesDvlaApi(
        @Named("FlexRetrofit") retrofit: Retrofit
    ): DvlaApi {
        return retrofit.create(DvlaApi::class.java)
    }

    @Provides
    @Singleton
    @Named("dvla_prefs")
    fun provideDvlaDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile("dvla_prefs") }
        )
    }

    @Provides
    @Named("dvla_auth_url")
    fun provideDvlaAuthUrl(): String = BuildConfig.DVLA_AUTH_URL

}
