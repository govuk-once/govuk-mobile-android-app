package uk.gov.govuk.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.data.BuildConfig
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.AuthorizationInterceptor
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    @Named("FlexRetrofit")
    fun provideAuthenticateRetrofit(authRepo: AuthRepo): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(authRepo))
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.FLEX_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}