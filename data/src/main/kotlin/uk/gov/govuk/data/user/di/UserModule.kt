package uk.gov.govuk.data.user.di

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
import uk.gov.govuk.data.user.remote.UserApi
import javax.inject.Singleton

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
