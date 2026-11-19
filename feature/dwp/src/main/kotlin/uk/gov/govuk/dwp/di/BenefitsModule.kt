package uk.gov.govuk.dwp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.AuthorizationInterceptor
import uk.gov.govuk.dwp.BuildConfig
import uk.gov.govuk.dwp.data.remote.PaymentsApi
import uk.gov.govuk.dwp.data.remote.PersonalApi

@InstallIn(SingletonComponent::class)
@Module
internal class BenefitsModule {
    @Provides
    @Singleton
    fun providesPaymentsApi(
        authRepo: AuthRepo
    ): PaymentsApi {
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(AuthorizationInterceptor(authRepo))
                .build()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.PAYMENTS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PaymentsApi::class.java)
    }

    @Provides
    @Singleton
    fun providesPersonalApi(
        authRepo: AuthRepo
    ): PersonalApi {
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(AuthorizationInterceptor(authRepo))
                .build()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.PERSONAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PersonalApi::class.java)
    }
}
