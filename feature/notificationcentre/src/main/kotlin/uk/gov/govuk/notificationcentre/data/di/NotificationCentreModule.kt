package uk.gov.govuk.notificationcentre.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import uk.gov.govuk.notificationcentre.DefaultNotificationCentreFeature
import uk.gov.govuk.notificationcentre.NotificationCentreFeature
import uk.gov.govuk.notificationcentre.data.DateProvider
import uk.gov.govuk.notificationcentre.data.DateProviderImpl
import uk.gov.govuk.notificationcentre.data.NotificationCentreRepo
import uk.gov.govuk.notificationcentre.data.NotificationCentreRepoImpl
import uk.gov.govuk.notificationcentre.data.remote.NotificationCentreApi
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class NotificationCentreScope

@InstallIn(SingletonComponent::class)
@Module
internal object NotificationCentreModule {
    @Provides
    @Singleton
    fun providesNotificationCentreApi(@Named("FlexRetrofit") retrofit: Retrofit): NotificationCentreApi =
        retrofit.create(NotificationCentreApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationCentreRepo(notificationCentreRepo: NotificationCentreRepoImpl): NotificationCentreRepo {
        return notificationCentreRepo
    }

    @Provides
    @Singleton
    fun providesNotificationCentreFeature(notificationCentreRepo: NotificationCentreRepo): NotificationCentreFeature {
        return DefaultNotificationCentreFeature(notificationCentreRepo)
    }

    @Provides
    @Singleton
    fun provideDateProvider(): DateProvider {
        return DateProviderImpl()
    }

    @Provides
    @Singleton
    @NotificationCentreScope
    fun provideCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

}

