package uk.gov.govuk.data.notificationcentre.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.data.notificationcentre.remote.NotificationCentreApi
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NotificationCentreModule {
    @Provides
    @Singleton
    fun providesNotificationCentreApi(@Named("FlexRetrofit") retrofit: Retrofit): NotificationCentreApi =
        retrofit.create(NotificationCentreApi::class.java)
}

