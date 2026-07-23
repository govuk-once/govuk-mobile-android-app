package uk.gov.govuk.notificationcentre.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.notificationcentre.DefaultNotificationCentreFeature
import uk.gov.govuk.notificationcentre.NotificationCentreFeature
import uk.gov.govuk.notificationcentre.data.DateProvider
import uk.gov.govuk.notificationcentre.data.DateProviderImpl
import uk.gov.govuk.notificationcentre.data.NotificationCentreRepo
import uk.gov.govuk.notificationcentre.data.NotificationCentreRepoImpl
import uk.gov.govuk.notificationcentre.data.remote.NotificationCentreApi
import javax.inject.Named
import javax.inject.Singleton

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

}

