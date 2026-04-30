package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName

data class VehicleEnquiryResponse(
    @SerializedName("registrationNumber")
    val registrationNumber: String,
    @SerializedName("taxStatus")
    val taxStatus: TaxStatus? = null,
    @SerializedName("taxDueDate")
    val taxDueDate: String? = null,     // YYYY-MM-dd
    @SerializedName("artEndDate")
    val artEndDate: String? = null,     // YYYY-MM-dd
    @SerializedName("motStatus")
    val motStatus: MotStatus? = null,
    @SerializedName("motExpiryDate")
    val motExpiryDate: String? = null,
    @SerializedName("make")
    val make: String? = null,
    @SerializedName("monthOfFirstDvlaRegistration")
    val monthOfFirstDvlaRegistration: String? = null,       // YYYY-MM
    @SerializedName("monthOfFirstRegistration")
    val monthOfFirstRegistration: String? = null,       // YYYY-MM
    @SerializedName("yearOfManufacture")
    val yearOfManufacture: Int? = null,
    @SerializedName("engineCapacity")
    val engineCapacity: Int? = null,
    @SerializedName("co2Emissions")
    val co2Emissions: Int? = null,
    @SerializedName("fuelType")
    val fuelType: String? = null,
    @SerializedName("markedForExport")
    val markedForExport: Boolean? = null,
    @SerializedName("colour")
    val colour: String? = null,
    @SerializedName("typeApproval")
    val typeApproval: String? = null,
    @SerializedName("wheelplan")
    val wheelplan: String? = null,
    @SerializedName("revenueWeight")
    val revenueWeight: Int? = null,
    @SerializedName("realDrivingEmissions")
    val realDrivingEmissions: String? = null,
    @SerializedName("dateOfLastV5CIssued")
    val dateOfLastV5CIssued: String? = null,    // YYYY-MM-dd
    @SerializedName("euroStatus")
    val euroStatus: String? = null,
    @SerializedName("automatedVehicle")
    val automatedVehicle: Boolean? = null
)

enum class TaxStatus {
    @SerializedName("Not Taxed for on Road Use")
    NOT_TAXED_FOR_ON_ROAD_USE,

    @SerializedName("SORN")
    SORN,

    @SerializedName("Taxed")
    TAXED,

    @SerializedName("Untaxed")
    UNTAXED
}

enum class MotStatus {
    @SerializedName("No details held by DVLA")
    NO_DETAILS_HELD,

    @SerializedName("No results returned")
    NO_RESULTS_RETURNED,

    @SerializedName("Not valid")
    NOT_VALID,

    @SerializedName("Valid")
    VALID
}
data class ErrorResponse(
    @SerializedName("errors")
    val errors: List<ErrorDetail>? = null
)

data class ErrorDetail(
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("detail")
    val detail: String? = null
)
