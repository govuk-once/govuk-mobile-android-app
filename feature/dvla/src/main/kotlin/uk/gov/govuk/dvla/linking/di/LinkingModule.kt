package uk.gov.govuk.dvla.linking.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.dvla.BuildConfig.LINKING_BASE_URL
import uk.gov.govuk.dvla.linking.remote.LinkingApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object LinkingModule {

    @Provides
    @Singleton
    fun provideLinkingApi(): LinkingApi {
        return Retrofit.Builder()
            .baseUrl(LINKING_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LinkingApi::class.java)
    }
}
