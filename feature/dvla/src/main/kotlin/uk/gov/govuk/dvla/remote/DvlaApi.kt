package uk.gov.govuk.dvla.remote

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import uk.gov.govuk.dvla.remote.model.CustomerSummaryResponse
import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse
import uk.gov.govuk.dvla.remote.model.LicenceResponse
import uk.gov.govuk.dvla.remote.model.LinkStatusResponse
import uk.gov.govuk.dvla.remote.model.MultiShareCodeResponse
import uk.gov.govuk.dvla.remote.model.SingleShareCodeResponse
import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse

interface DvlaApi {

    @POST("app/udp/v1/identity/dvla")
    suspend fun linkDvlaIdentity(@Header("x-linking-token") id: String): Response<Unit>

    @DELETE("app/udp/v1/identity/dvla")
    suspend fun deleteDvlaIdentity(): Response<Unit>

    @GET("app/dvla/v1/driving-licence")
    suspend fun getDrivingLicence(): Response<LicenceResponse>

    @GET("app/dvla/v1/driver-summary")
    suspend fun getDriverSummary(): Response<DriverSummaryResponse>

    @GET("app/dvla/v1/customer-summary")
    suspend fun getCustomerSummary(): Response<CustomerSummaryResponse>

    @GET("app/dvla/v1/vehicle-enquiry/{reg}")
    suspend fun lookupVehicle(@Path("reg") registrationNumber: String): Response<VehicleEnquiryResponse>

    @POST("app/dvla/v1/share-code")
    suspend fun createShareCode(): Response<SingleShareCodeResponse>

    @GET("app/dvla/v1/share-codes")
    suspend fun getShareCodes(): Response<MultiShareCodeResponse>

    @POST("app/dvla/v1/share-code/{id}/cancel")
    suspend fun cancelShareCode(@Path("id") tokenId: String): Response<SingleShareCodeResponse>
}
