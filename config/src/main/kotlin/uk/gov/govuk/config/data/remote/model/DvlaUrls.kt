package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class DvlaUrls(
    @SerializedName("addVehicle") val addVehicle: String,
    @SerializedName("renewLicence") val renewLicence: String,
    @SerializedName("soldVehicle") val soldVehicle: String,
    @SerializedName("sornRules") val sornRules: String,
    @SerializedName("makeSorn") val makeSorn: String,
    @SerializedName("getLogbook") val getLogbook: String,
    @SerializedName("changeLogbookAddress") val changeLogbookAddress: String,
    @SerializedName("cancelTax") val cancelTax: String,
    @SerializedName("changeLicenceAddress") val changeLicenceAddress: String,
    @SerializedName("changeNameGenderLicence") val changeNameGenderLicence: String,
    @SerializedName("replaceLicence") val replaceLicence: String,
    @SerializedName("manageTaxPayment") val manageTaxPayment: String,
    @SerializedName("taxVehicle") val taxVehicle: String,
    @SerializedName("historicVehicles") val historicVehicles: String,
    @SerializedName("checkMot") val checkMot: String,
    @SerializedName("driverDetails") val driverDetails: String,
    @SerializedName("account") val account: String,
    @SerializedName("drivingRecord") val drivingRecord: String
)
