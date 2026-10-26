package uk.gov.govuk.login.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.govuk.data.auth.EphemeralBrowsingProvider
import uk.gov.govuk.login.EphemeralCustomTabsProvider
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class LoginModule {

    @Singleton
    @Provides
    @Named("login_prefs")
    fun provideLoginDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile("login_prefs") }
        )
    }
}

@InstallIn(SingletonComponent::class)
@Module
internal abstract class LoginBindsModule {

    @Binds
    @Singleton
    abstract fun bindEphemeralBrowsingProvider(
        impl: EphemeralCustomTabsProvider
    ): EphemeralBrowsingProvider
}