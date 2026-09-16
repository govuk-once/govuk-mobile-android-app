package uk.gov.govuk.travelalerts.data.remote

import retrofit2.Response
import retrofit2.http.GET
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group


fun interface TravelApi {
    companion object {
        private const val COUNTRIES_PATH = "/app/travel/v1/countries"
    }
    @GET(COUNTRIES_PATH)
    suspend fun getCountries(): Response<List<Country>>
}