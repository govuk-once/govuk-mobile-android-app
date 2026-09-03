package uk.gov.govuk.travelalerts.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.travelalerts.data.DateProvider
import uk.gov.govuk.travelalerts.data.DateProviderImpl
import uk.gov.govuk.travelalerts.data.TravelAlertsRepoImpl
import uk.gov.govuk.travelalerts.data.remote.GroupsApi
import uk.gov.govuk.travelalerts.DefaultTravelAlertsFeature
import uk.gov.govuk.travelalerts.TravelAlertsFeature
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.remote.TravelApi
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object TravelAlertsModule {
    @Provides
    @Singleton
    fun providesGroupsApi(@Named("FlexRetrofit") retrofit: Retrofit): GroupsApi =
        retrofit.create(GroupsApi::class.java)

    @Provides
    @Singleton
    fun providesTravelApi(@Named("FlexRetrofit") retrofit: Retrofit): TravelApi =
        retrofit.create(TravelApi::class.java)

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

