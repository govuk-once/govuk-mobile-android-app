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
    suspend fun linkAccount(id: String): Result<Unit> {
        return safeAuthApiCall(
            apiCall = { api.linkDvlaIdentity(id) },
            authRepo = authRepo
        )
    }
}