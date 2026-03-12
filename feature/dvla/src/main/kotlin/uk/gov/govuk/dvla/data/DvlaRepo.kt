package uk.gov.govuk.dvla.data

import uk.gov.govuk.data.remote.safeApiCall
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.data.model.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DvlaRepo @Inject constructor(
    private val api: DvlaApi
) {
    suspend fun linkAccount(id: String): Result<Unit> {
        return safeApiCall { api.linkDvlaIdentity(id) }
    }
}