package uk.gov.govuk.data.identity.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.data.identity.remote.IdentityApi
import uk.gov.govuk.data.user.remote.UserApi
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object IdentityModule {

    @Provides
    @Singleton
    fun providesIdentityApi(@Named("FlexRetrofit") retrofit: Retrofit): IdentityApi =
        retrofit.create(IdentityApi::class.java)
}

