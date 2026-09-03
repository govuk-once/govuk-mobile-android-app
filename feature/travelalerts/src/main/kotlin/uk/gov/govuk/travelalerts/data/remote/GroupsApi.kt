package uk.gov.govuk.travelalerts.data.remote

import retrofit2.Response
import retrofit2.http.GET
import uk.gov.govuk.travelalerts.data.model.Group


interface GroupsApi {
    companion object {
        private const val GROUPS_PATH = "/app/groups/v1/groups"
    }
    @GET(GROUPS_PATH)
    suspend fun getGroups(): Response<List<Group>>
}