# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# remote/model/CustomerVehiclesResponse.kt
-keep class uk.gov.govuk.dvla.remote.model.CustomerVehiclesResponse
-keep class uk.gov.govuk.dvla.remote.model.VehicleSummary

# remote/model/CustomerVehicleDetailsResponse.kt
-keep class uk.gov.govuk.dvla.remote.model.CustomerVehicleDetailsResponse
-keep class uk.gov.govuk.dvla.remote.model.CustomerVehicleDetails

# remote/model/LicenceResponse.kt
-keep class uk.gov.govuk.dvla.remote.model.LicenceResponse
-keep class uk.gov.govuk.dvla.remote.model.DrivingLicence
-keep class uk.gov.govuk.dvla.remote.model.common.LicenceType
-keep class uk.gov.govuk.dvla.remote.model.common.LicenceStatus

# remote/model/DvlaErrorBody.kt
-keep class uk.gov.govuk.dvla.remote.model.DvlaErrorBody

# remote/model/LinkStatusResponse.kt
-keep class uk.gov.govuk.dvla.remote.model.LinkStatusResponse

# remote/model/ShareCodeReponse.kt
-keep class uk.gov.govuk.dvla.remote.model.SingleShareCodeResponse
-keep class uk.gov.govuk.dvla.remote.model.MultiShareCodeResponse
-keep class uk.gov.govuk.dvla.remote.model.ShareCode
-keep class uk.gov.govuk.dvla.remote.model.ShareCodeValidity
-keep class uk.gov.govuk.dvla.remote.model.ShareCodeActivationStatus

# remote/model/VehicleEnquiryResponse.kt
-keep class uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse
-keep class uk.gov.govuk.dvla.remote.model.ErrorResponse
-keep class uk.gov.govuk.dvla.remote.model.ErrorDetail

# remote/model/common
-keep class uk.gov.govuk.dvla.remote.model.common.Application
-keep class uk.gov.govuk.dvla.remote.model.common.AvailableAction
-keep class uk.gov.govuk.dvla.remote.model.common.DriversEligibility
-keep class uk.gov.govuk.dvla.remote.model.common.FuelType
-keep class uk.gov.govuk.dvla.remote.model.common.MotStatus
-keep class uk.gov.govuk.dvla.remote.model.common.PossibleTransaction
-keep class uk.gov.govuk.dvla.remote.model.common.TaxStatus
-keep class uk.gov.govuk.dvla.remote.model.common.VehicleColour
