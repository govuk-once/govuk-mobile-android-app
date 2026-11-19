package uk.gov.govuk.dwp.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dwp.data.local.AuthDataStore
import uk.gov.govuk.dwp.remote.AuthApi

@Singleton
class AuthLinkRepo @Inject internal constructor(
    private val api: AuthApi,
    private val authRepo: AuthRepo,
    private val authDataStore: AuthDataStore
) {
    val linkState = authDataStore.linkState

    internal suspend fun linkAccount(
        token: String
    ): Result<Unit> = try {
        val result = safeAuthApiCall({ api.linkDwpIdentity(token) }, authRepo)
        if (result is Result.Success) authDataStore.markLinked()
        result
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        Result.Error()
    }

    suspend fun clear() = authDataStore.clear()
}
