package uk.gov.govuk.search.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.search.DefaultSearchFeature
import uk.gov.govuk.search.SearchFeature
import uk.gov.govuk.search.data.SearchRepo
import uk.gov.govuk.search.data.remote.AutocompleteApi
import uk.gov.govuk.search.data.remote.SearchApi
import uk.gov.govuk.search.domain.SearchConfig
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class SearchModule {

    @Provides
    @Singleton
    fun providesSearchFeature(searchRepo: SearchRepo): SearchFeature {
        return DefaultSearchFeature(searchRepo)
    }

    @Provides
    @Singleton
    fun providesSearchApi(@ApplicationContext context: Context): SearchApi {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(SearchConfig.API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApi::class.java)
    }

    @Provides
    @Singleton
    fun providesAutocompleteApi(@ApplicationContext context: Context): AutocompleteApi {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(SearchConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AutocompleteApi::class.java)
    }

    private class UserAgentInterceptor(@ApplicationContext context: Context): Interceptor {
        private val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        private val userAgent = "govuk_android/$appVersion"

        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
            )
        }
    }
}
