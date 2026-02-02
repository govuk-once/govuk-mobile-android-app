package uk.gov.govuk.notifications.di

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
import uk.gov.govuk.notifications.OneSignalClient
import uk.gov.govuk.notifications.NotificationsProvider
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NotificationsModule {

    @Singleton
    @Provides
    @Named("notification_prefs")
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile("notification_prefs") }
        )
    }

    @Singleton
    @Provides
    fun provideNotificationsProvider(@ApplicationContext context: Context): NotificationsProvider =
        OneSignalClient(context)
}
