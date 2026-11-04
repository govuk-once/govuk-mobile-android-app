package uk.gov.govuk.data.di

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
import uk.gov.govuk.data.crypto.CryptoProvider
import uk.gov.govuk.data.crypto.TinkClient
import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.UserRepoImpl

@InstallIn(SingletonComponent::class)
@Module
internal class DataModule {

    @Provides
    @Singleton
    fun provideUserRepo(userRepo: UserRepoImpl): UserRepo = userRepo

    @Singleton
    @Provides
    @Named("database_prefs")
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        produceFile = { context.preferencesDataStoreFile("database_preferences") }
    )

    @Singleton
    @Provides
    @Named("token_prefs")
    fun provideTokenPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        produceFile = { context.preferencesDataStoreFile("token_preferences") }
    )

    @Singleton
    @Provides
    fun provideCryptoProvider(@ApplicationContext context: Context): CryptoProvider = TinkClient(context)
}
