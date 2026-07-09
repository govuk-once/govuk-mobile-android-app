package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.remote.model.common.LicenceStatus
import uk.gov.govuk.dvla.remote.model.common.LicenceType

data class LicenceResponse(
    @SerializedName("customerDrivingLicence") val drivingLicence: DrivingLicence
) {
    data class DrivingLicence(
        @SerializedName("licenceType") val licenceType: LicenceType,
        @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String,
        @SerializedName("driverTitle") val driverTitle: String?,
        @SerializedName("driverFirstNames") val driverFirstNames: String?,
        @SerializedName("driverLastName") val driverLastName: String?,
        @SerializedName("driverFullAddress") val driverFullAddress: String?,
        @SerializedName("tokenValidToDate") val tokenValidToDate: String?,
        @SerializedName("licenceStatus") val licenceStatus: LicenceStatus
    )
}
