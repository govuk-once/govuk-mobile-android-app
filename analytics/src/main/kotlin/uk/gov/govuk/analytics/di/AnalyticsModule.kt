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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.govuk.analytics.AnalyticsCoordinator
import uk.gov.govuk.analytics.FirebaseAnalyticsClient
import uk.gov.govuk.analytics.QualtricsAnalyticsClient
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics = Firebase.crashlytics

    @Provides
    @Singleton
    fun provideQualtrics(): Qualtrics = Qualtrics.instance()

    @Provides
    @Singleton
    fun provideAnalyticsCoordinator(@ApplicationContext context: Context): AnalyticsCoordinator {
        return AnalyticsCoordinator(
            firebaseAnalyticsClient = FirebaseAnalyticsClient(
                firebaseAnalytics = Firebase.analytics,
                firebaseCrashlytics = Firebase.crashlytics
            ),
            qualtricsAnalyticsClient = QualtricsAnalyticsClient(
                context = context,
                qualtrics = Qualtrics.instance()
            )
        )
    }

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
