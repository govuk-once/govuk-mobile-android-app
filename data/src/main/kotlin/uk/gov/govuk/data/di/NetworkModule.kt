package uk.gov.govuk.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.data.BuildConfig
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.AuthorizationInterceptor
import uk.gov.govuk.data.remote.DvlaStubInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

internal class HeaderInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()
        currentRequest.addHeader("Content-Type", "application/json")
        val newRequest = currentRequest.build()
        return chain.proceed(newRequest)
    }
}

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    @Named("FlexRetrofit")
    fun provideAuthenticateRetrofit(authRepo: AuthRepo): Retrofit {
        val client = OkHttpClient.Builder()
            // Todo - remove!!!
            .apply { if (BuildConfig.DEBUG) addInterceptor(DvlaStubInterceptor()) }
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(AuthorizationInterceptor(authRepo))
            // TODO: Consider removing below custom timeouts when Flex is stable
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.FLEX_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}