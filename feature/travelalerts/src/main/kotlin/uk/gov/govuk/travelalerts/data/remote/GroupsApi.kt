package uk.gov.govuk.travelalerts.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import uk.gov.govuk.travelalerts.data.model.Group
import uk.gov.govuk.travelalerts.data.model.SubscriptionRequest

interface GroupsApi {
    companion object {
        private const val GROUPS_PATH = "/app/groups/v1/groups"
    }

    @GET(GROUPS_PATH)
    suspend fun getGroups(): Response<List<Group>>

    @POST(GROUPS_PATH)
    suspend fun subscribeToGroups(@Body subscriptions: List<SubscriptionRequest>): Response<Unit>
}