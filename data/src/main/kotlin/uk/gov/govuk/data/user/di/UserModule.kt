package uk.gov.govuk.data.user.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.data.user.remote.UserApi
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UserModule {

    @Provides
    @Singleton
    fun providesUserApi(@Named("FlexRetrofit") retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)
}

