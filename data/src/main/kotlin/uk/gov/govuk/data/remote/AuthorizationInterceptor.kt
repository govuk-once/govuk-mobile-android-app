package uk.gov.govuk.data.remote

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import uk.gov.govuk.data.auth.AuthRepo

class AuthorizationInterceptor @Inject constructor(
    private val authRepo: AuthRepo
) : Interceptor {
    override fun intercept(
        chain: Interceptor.Chain
    ): Response {
        val currentRequest = chain.request().newBuilder()
        currentRequest.addHeader("Authorization", "Bearer ${authRepo.getAccessToken()}")

        val newRequest = currentRequest.build()
        return chain.proceed(newRequest)
    }
}
