package uk.gov.govuk.analytics.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.qualtrics.digital.Qualtrics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.govuk.analytics.ActivityProvider
import uk.gov.govuk.analytics.ActivityProviderInterface
import uk.gov.govuk.analytics.AnalyticsCoordinator
import uk.gov.govuk.analytics.AnalyticsCoordinatorInterface
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsCoordinator(
        analyticsCoordinator: AnalyticsCoordinator
    ): AnalyticsCoordinatorInterface

    @Binds
    @Singleton
    abstract fun bindActivityProvider(
        activityProvider: ActivityProvider
    ): ActivityProviderInterface

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

        @Provides
        @Singleton
        fun provideCrashlytics(): FirebaseCrashlytics = Firebase.crashlytics

        @Provides
        @Singleton
        fun provideQualtrics(): Qualtrics = Qualtrics.instance()

        @Singleton
        @Provides
        @Named("analytics_prefs")
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                produceFile = { context.preferencesDataStoreFile("analytics_preferences") }
            )
        }
    }
}
