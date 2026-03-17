package uk.gov.govuk.dvla.data

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeAuthApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DvlaRepo @Inject constructor(
    private val api: DvlaApi,
    private val authRepo: AuthRepo
) {
    var isLinked = false
        private set

    suspend fun linkAccount(id: String): Result<Unit> {
        val result = safeAuthApiCall({ api.linkDvlaIdentity(id) }, authRepo)
        isLinked = result is Result.Success
        return result
    }

    suspend fun unlinkAccount(): Result<Unit> {
        val result = safeAuthApiCall({ api.deleteDvlaIdentity() }, authRepo)
        isLinked = result !is Result.Success
        return result
    }
}