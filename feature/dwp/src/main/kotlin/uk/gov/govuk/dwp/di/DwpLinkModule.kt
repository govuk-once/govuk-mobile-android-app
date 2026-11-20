package uk.gov.govuk.dwp.di

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
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Response
import uk.gov.govuk.dwp.BuildConfig
import uk.gov.govuk.dwp.remote.DwpLinkApi

@Module
@InstallIn(SingletonComponent::class)
internal object DwpLinkModule {
    const val TEMPORARY_LINKING_TOKEN = "temporary-dwp-linking-token"

    @Provides
    @Singleton
    fun provideAuthApi(): DwpLinkApi = DwpLinkApi { _ ->
        // TODO: Replace this local API binding with the authenticated Retrofit DwpLinkApi,
        // as in DVLA's module, once the identity-linking endpoint is confirmed.
        Response.success(204, Unit)
    }

    @Provides
    @Singleton
    @Named("dwp_prefs")
    fun provideDwpDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        produceFile = { context.preferencesDataStoreFile("dwp_prefs") }
    )

    @Provides
    @Named("dwp_auth_url")
    fun provideDwpAuthUrl(): String = BuildConfig.DWP_AUTH_URL
}
