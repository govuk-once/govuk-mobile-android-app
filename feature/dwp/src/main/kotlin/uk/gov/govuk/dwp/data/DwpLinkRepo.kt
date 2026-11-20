package uk.gov.govuk.dwp.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dwp.data.local.DwpDataStore
import uk.gov.govuk.dwp.linking.remote.LinkingApi
import uk.gov.govuk.dwp.linking.remote.model.VerificationRequest
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse
import uk.gov.govuk.dwp.remote.DwpLinkApi

@Singleton
class DwpLinkRepo @Inject internal constructor(
    private val api: DwpLinkApi,
    private val linkingApi: LinkingApi,
    private val authRepo: AuthRepo,
    private val dwpDataStore: DwpDataStore
) {
    val linkState = dwpDataStore.linkState

    internal suspend fun getVerification(): Result<VerificationResponse> = safeAuthApiCall(
        { linkingApi.getVerification(VerificationRequest(authRepo.getAccessToken())) },
        authRepo
    )

    internal suspend fun linkAccount(
        token: String
    ): Result<Unit> = try {
        val result = safeAuthApiCall({ api.linkDwpIdentity(token) }, authRepo)
        if (result is Result.Success) dwpDataStore.markLinked()
        result
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        Result.Error()
    }

    suspend fun clear() = dwpDataStore.clear()
}
