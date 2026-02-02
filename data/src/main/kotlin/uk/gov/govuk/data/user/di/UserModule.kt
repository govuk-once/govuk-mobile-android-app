package uk.gov.govuk.data.user.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.data.BuildConfig
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.user.remote.UserApi
import javax.inject.Inject
import javax.inject.Singleton

internal class AuthorizationInterceptor @Inject constructor(
    private val authRepo: AuthRepo
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()
        currentRequest.addHeader("Authorization", "Bearer ${authRepo.getAccessToken()}")

        val newRequest = currentRequest.build()
        return chain.proceed(newRequest)
    }
}

@InstallIn(SingletonComponent::class)
@Module
class UserModule {
    @Provides
    @Singleton
    fun providesUserApi(authRepo: AuthRepo): UserApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(authRepo))
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.USER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UserApi::class.java)
    }
}
