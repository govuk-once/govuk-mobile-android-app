package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName

data class DriverSummaryResponse(
    @SerializedName("linkingId") val linkingId: String,
    @SerializedName("driverViewResponse") val driverViewResponse: DriverViewResponse,
    @SerializedName("sdlResponse") val sdlResponse: SdlResponse,
    @SerializedName("driversEligibilityResponse") val driversEligibilityResponse: DriversEligibilityResponse,
    @SerializedName("imageUtilityResponse") val imageUtilityResponse: ImageUtilityResponse,
    @SerializedName("hasErrors") val hasErrors: Boolean
)

data class DriverViewResponse(
    @SerializedName("driver") val driver: Driver,
    @SerializedName("licence") val licence: Licence,
    @SerializedName("entitlement") val entitlement: List<Entitlement>,
    @SerializedName("testPass") val testPass: List<TestPass>,
    @SerializedName("endorsements") val endorsements: List<Any>,
    @SerializedName("token") val token: Token,
    @SerializedName("holder") val holder: Any?
)

data class Driver(
    @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String,
    @SerializedName("firstNames") val firstNames: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("title") val title: String,
    @SerializedName("nameFormat") val nameFormat: String,
    @SerializedName("fullModeOfAddress") val fullModeOfAddress: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("placeOfBirth") val placeOfBirth: String,
    @SerializedName("address") val address: Address,
    @SerializedName("disqualifiedUntil") val disqualifiedUntil: String?,
    @SerializedName("disqualifiedForLife") val disqualifiedForLife: Boolean?,
    @SerializedName("deathNotificationDate") val deathNotificationDate: String?,
    @SerializedName("disqualifiedPendingSentence") val disqualifiedPendingSentence: Boolean?,
    @SerializedName("eyesight") val eyesight: String,
    @SerializedName("hearing") val hearing: String?,
    @SerializedName("imagesExist") val imagesExist: Boolean,
    @SerializedName("isMilitary") val isMilitary: Boolean?,
    @SerializedName("approvedDrivingInstructor") val approvedDrivingInstructor: Boolean?,
    @SerializedName("retainedC1_D1Entitlement") val retainedC1D1Entitlement: Boolean?,
    @SerializedName("previousDrivingLicence") val previousDrivingLicence: List<PreviousDrivingLicence>,
    @SerializedName("dateRevokedUntilTestPassed") val dateRevokedUntilTestPassed: String?,
    @SerializedName("penaltyPoints") val penaltyPoints: Int,
    @SerializedName("numberOfOffences") val numberOfOffences: Int
)

data class Address(
    @SerializedName("unstructuredAddress") val unstructuredAddress: UnstructuredAddress
)

data class UnstructuredAddress(
    @SerializedName("language") val language: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("dps") val dps: String?,
    @SerializedName("line1") val line1: String,
    @SerializedName("line2") val line2: String?,
    @SerializedName("line3") val line3: String?,
    @SerializedName("line4") val line4: String?,
    @SerializedName("line5") val line5: String,
    @SerializedName("postcode") val postcode: String
)

data class PreviousDrivingLicence(
    @SerializedName("previousDrivingLicenceNumber") val previousDrivingLicenceNumber: String,
    @SerializedName("previousLastName") val previousLastName: String,
    @SerializedName("previousFirstNames") val previousFirstNames: String,
    @SerializedName("previousDateOfBirth") val previousDateOfBirth: String
)

data class Licence(
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusQualifier") val statusQualifier: String?,
    @SerializedName("countryToWhichExchanged") val countryToWhichExchanged: String?
)

data class Entitlement(
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("categoryLegalLiteral") val categoryLegalLiteral: String,
    @SerializedName("welshCategoryLegalLiteral") val welshCategoryLegalLiteral: String?,
    @SerializedName("categoryShortLiteral") val categoryShortLiteral: String,
    @SerializedName("welshCategoryShortLiteral") val welshCategoryShortLiteral: String?,
    @SerializedName("categoryType") val categoryType: String,
    @SerializedName("fromDate") val fromDate: String,
    @SerializedName("fromDateIsPriorTo") val fromDateIsPriorTo: Boolean?,
    @SerializedName("expiryDate") val expiryDate: String,
    @SerializedName("categoryStatus") val categoryStatus: String,
    @SerializedName("restrictions") val restrictions: List<Restriction>,
    @SerializedName("restrictedToAutomaticTransmission") val restrictedToAutomaticTransmission: Boolean?,
    @SerializedName("fromNonGB") val fromNonGB: Boolean?
)

data class Restriction(
    @SerializedName("restrictionCode") val restrictionCode: String,
    @SerializedName("restrictionLiteral") val restrictionLiteral: String,
    @SerializedName("welshRestrictionLiteral") val welshRestrictionLiteral: String?
)

data class TestPass(
    @SerializedName("type") val type: String,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("categoryLegalLiteral") val categoryLegalLiteral: String,
    @SerializedName("categoryShortLiteral") val categoryShortLiteral: String,
    @SerializedName("testDate") val testDate: String,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("withAutomaticTransmission") val withAutomaticTransmission: Boolean?,
    @SerializedName("vehicleAdaptations") val vehicleAdaptations: List<String>,
    @SerializedName("restrictions") val restrictions: List<Restriction>,
    @SerializedName("withTrailer") val withTrailer: Boolean?,
    @SerializedName("extendedTest") val extendedTest: Boolean?,
    @SerializedName("licenceSurrendered") val licenceSurrendered: Boolean?,
    @SerializedName("testingAuthority") val testingAuthority: String?
)

data class Token(
    @SerializedName("type") val type: String,
    @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String,
    @SerializedName("issueNumber") val issueNumber: String,
    @SerializedName("validFromDate") val validFromDate: String,
    @SerializedName("validToDate") val validToDate: String,
    @SerializedName("isProvisional") val isProvisional: Boolean?,
    @SerializedName("entitlements") val entitlements: List<TokenEntitlement>
)

data class TokenEntitlement(
    @SerializedName("category") val category: String,
    @SerializedName("categoryLegalLiteral") val categoryLegalLiteral: String,
    @SerializedName("categoryShortLiteral") val categoryShortLiteral: String,
    @SerializedName("categoryType") val categoryType: String,
    @SerializedName("categoryFromDate") val categoryFromDate: String,
    @SerializedName("categoryFromDateIsPriorTo") val categoryFromDateIsPriorTo: Boolean?,
    @SerializedName("categoryExpiryDate") val categoryExpiryDate: String,
    @SerializedName("categoryRestrictions") val categoryRestrictions: List<TokenRestriction>,
    @SerializedName("group") val group: String?,
    @SerializedName("groupShortLiteral") val groupShortLiteral: String?,
    @SerializedName("groupLegalLiteral") val groupLegalLiteral: String?,
    @SerializedName("groupType") val groupType: String?,
    @SerializedName("groupFromDate") val groupFromDate: String?,
    @SerializedName("groupFromDateIsPrior") val groupFromDateIsPrior: Boolean?,
    @SerializedName("groupExpiryDate") val groupExpiryDate: String?,
    @SerializedName("groupRestrictions") val groupRestrictions: List<TokenRestriction>
)

data class TokenRestriction(
    @SerializedName("categoryRestrictionCode") val categoryRestrictionCode: String,
    @SerializedName("categoryRestrictionLiteral") val categoryRestrictionLiteral: String,
    @SerializedName("welshCategoryRestrictionLiteral") val welshCategoryRestrictionLiteral: String?
)

data class SdlResponse(
    @SerializedName("tokens") val tokens: List<Any>
)

data class DriversEligibilityResponse(
    @SerializedName("applications") val applications: List<Application>
)

data class Application(
    @SerializedName("applicationType") val applicationType: String,
    @SerializedName("isRequired") val isRequired: Boolean,
    @SerializedName("ineligibleReason") val ineligibleReason: String?,
    @SerializedName("availableActions") val availableActions: List<AvailableAction>,
    @SerializedName("possibleTransactions") val possibleTransactions: List<PossibleTransaction>
)

data class AvailableAction(
    @SerializedName("actionType") val actionType: String,
    @SerializedName("isRequired") val isRequired: Boolean?
)

data class PossibleTransaction(
    @SerializedName("transactionType") val transactionType: String,
    @SerializedName("isRequired") val isRequired: Boolean
)

data class ImageUtilityResponse(
    @SerializedName("photoUrl") val photoUrl: String,
    @SerializedName("signatureImageUrl") val signatureImageUrl: String
)