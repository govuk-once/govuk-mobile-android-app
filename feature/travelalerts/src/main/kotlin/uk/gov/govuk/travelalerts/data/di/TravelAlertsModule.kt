package uk.gov.govuk.travelalerts.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.travelalerts.data.DateProvider
import uk.gov.govuk.travelalerts.data.DateProviderImpl
import uk.gov.govuk.travelalerts.data.TravelAlertsRepoImpl
import uk.gov.govuk.travelalerts.data.remote.TravelAlertsApi
import uk.gov.govuk.travelalerts.DefaultTravelAlertsFeature
import uk.gov.govuk.travelalerts.TravelAlertsFeature
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object TravelAlertsModule {
    @Provides
    @Singleton
    fun providesTravelAlertsApi(@Named("FlexRetrofit") retrofit: Retrofit): TravelAlertsApi =
        retrofit.create(TravelAlertsApi::class.java)

    @Provides
    @Singleton
    fun providesTravelAlertsRepo(travelAlertsRepo: TravelAlertsRepoImpl): TravelAlertsRepo {
        return travelAlertsRepo
    }

    @Provides
    @Singleton
    fun providesTravelAlertsFeature(): TravelAlertsFeature {
        return DefaultTravelAlertsFeature()
    }

    @Provides
    @Singleton
    fun provideDateProvider(): DateProvider {
        return DateProviderImpl()
    }
}

