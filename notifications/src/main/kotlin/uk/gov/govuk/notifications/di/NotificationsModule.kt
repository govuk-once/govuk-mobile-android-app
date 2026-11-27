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
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.OneSignalClient
import uk.gov.govuk.notifications.navigation.DeepLinkLauncher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class NotificationsScope

@InstallIn(SingletonComponent::class)
@Module
class NotificationsModule {

    @Singleton
    @Provides
    @Named("notification_prefs")
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        produceFile = { context.preferencesDataStoreFile("notification_prefs") }
    )

    @Singleton
    @Provides
    fun provideNotificationsProvider(
        @ApplicationContext context: Context,
        deepLinkLauncher: DeepLinkLauncher
    ): NotificationsProvider = OneSignalClient(context, deepLinkLauncher)

    // Outlives any screen, so background work started from one (such as linking the device to
    // the user after consent) isn't cancelled when the user navigates away.
    @Singleton
    @Provides
    @NotificationsScope
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
