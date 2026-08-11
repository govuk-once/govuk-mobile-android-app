package uk.gov.govuk.travelalerts.data.remote

import retrofit2.Response
import retrofit2.http.GET
import uk.gov.govuk.travelalerts.data.model.Group


interface TravelAlertsApi {
    companion object {
        private const val NOTIFICATIONS_PATH = "/app/uns/v1/notifications"
    }
    @GET(NOTIFICATIONS_PATH)
    suspend fun getGroups(): Response<List<Group>>
}