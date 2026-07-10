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

# remote/model/DriverSummaryResponse.kt
-keep class uk.gov.govuk.dvla.remote.model.DriverSummaryResponse
-keep class uk.gov.govuk.dvla.remote.model.DriverView
-keep class uk.gov.govuk.dvla.remote.model.Driver
-keep class uk.gov.govuk.dvla.remote.model.Address
-keep class uk.gov.govuk.dvla.remote.model.UnstructuredAddress
-keep class uk.gov.govuk.dvla.remote.model.PreviousDrivingLicence
-keep class uk.gov.govuk.dvla.remote.model.Licence
-keep class uk.gov.govuk.dvla.remote.model.Entitlement
-keep class uk.gov.govuk.dvla.remote.model.Restriction
-keep class uk.gov.govuk.dvla.remote.model.TestPass
-keep class uk.gov.govuk.dvla.remote.model.Token
-keep class uk.gov.govuk.dvla.remote.model.TokenEntitlement
-keep class uk.gov.govuk.dvla.remote.model.TokenRestriction
-keep class uk.gov.govuk.dvla.remote.model.Sdl
-keep class uk.gov.govuk.dvla.remote.model.ImageUtility
-keep class uk.gov.govuk.dvla.remote.model.Endorsement
-keep class uk.gov.govuk.dvla.remote.model.Disqualification
-keep class uk.gov.govuk.dvla.remote.model.Intoxicant
-keep class uk.gov.govuk.dvla.remote.model.EndorsementMarker
-keep class uk.gov.govuk.dvla.remote.model.SdlToken
-keep class uk.gov.govuk.dvla.remote.model.HolderDetails
-keep class uk.gov.govuk.dvla.remote.model.HolderMarker
-keep class uk.gov.govuk.dvla.remote.model.TachoCard
-keep class uk.gov.govuk.dvla.remote.model.LicenceType
-keep class uk.gov.govuk.dvla.remote.model.LicenceStatus

# remote/model/LicenceResponse.kt
-keep class uk.gov.govuk.dvla.remote.model.LicenceResponse
-keep class uk.gov.govuk.dvla.remote.model.LicenceResponse$Driver
-keep class uk.gov.govuk.dvla.remote.model.LicenceResponse$Licence
-keep class uk.gov.govuk.dvla.remote.model.LicenceResponse$Token

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

# linking/remote/model
-keep class uk.gov.govuk.dvla.linking.remote.model.VerificationRequest
-keep class uk.gov.govuk.dvla.linking.remote.model.VerificationResponse
