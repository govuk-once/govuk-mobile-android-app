package uk.gov.govuk.dvla.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.dvla.remote.DvlaApi

import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DvlaModule {

    @Provides
    @Singleton
    fun providesDvlaApi(
        @Named("FlexRetrofit") retrofit: Retrofit
    ): DvlaApi {
        return retrofit.create(DvlaApi::class.java)
    }
}