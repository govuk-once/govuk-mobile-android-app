package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName

data class LicenceResponse(
    @SerializedName("driver") val driver: Driver,
    @SerializedName("licence") val licence: Licence,
    @SerializedName("token") val token: Token
) {
    data class Driver(
        @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String
    )

    data class Licence(
        @SerializedName("type") val type: String,
        @SerializedName("status") val status: String,
    )

    data class Token(
        @SerializedName("validFromDate") val validFromDate: String,
        @SerializedName("validToDate") val validToDate: String
    )
}
