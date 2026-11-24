package uk.gov.govuk.dwp.linking.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Response
import uk.gov.govuk.dwp.linking.remote.LinkingApi
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse

@Module
@InstallIn(SingletonComponent::class)
internal object LinkingModule {
    const val TEMPORARY_VERIFICATION_TOKEN = "temporary-dwp-verification-token"

    @Provides
    @Singleton
    fun provideLinkingApi(): LinkingApi = LinkingApi { _ ->
        // TODO: Replace this local API binding with Retrofit.create(LinkingApi::class.java),
        // as in DVLA's LinkingModule, after the verification base URL is confirmed.
        Response.success(VerificationResponse(TEMPORARY_VERIFICATION_TOKEN))
    }
}
