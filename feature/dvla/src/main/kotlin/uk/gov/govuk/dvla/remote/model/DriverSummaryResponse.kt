package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.remote.model.common.DriversEligibility

data class DriverSummaryResponse(
    @SerializedName("linkingId") val linkingId: String,
    @SerializedName("driverViewResponse") val driverView: DriverView,
    @SerializedName("sdlResponse") val sdl: Sdl?,
    @SerializedName("driversEligibilityResponse") val driversEligibility: DriversEligibility?,
    @SerializedName("imageUtilityResponse") val imageUtility: ImageUtility?,
    @SerializedName("hasErrors") val hasErrors: Boolean
)

data class DriverView(
    @SerializedName("driver") val driver: Driver,
    @SerializedName("licence") val licence: Licence?,
    @SerializedName("entitlement") val entitlement: List<Entitlement>,
    @SerializedName("testPass") val testPass: List<TestPass>,
    @SerializedName("endorsements") val endorsements: List<Endorsement>,
    @SerializedName("token") val token: Token?,
    @SerializedName("holder") val holder: HolderDetails?
)

data class Driver(
    @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String,
    @SerializedName("firstNames") val firstNames: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("nameFormat") val nameFormat: String?,
    @SerializedName("fullModeOfAddress") val fullModeOfAddress: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("dateOfBirth") val dateOfBirth: String?,
    @SerializedName("placeOfBirth") val placeOfBirth: String?,
    @SerializedName("address") val address: Address?,
    @SerializedName("disqualifiedUntil") val disqualifiedUntil: String?,
    @SerializedName("disqualifiedForLife") val disqualifiedForLife: Boolean?,
    @SerializedName("deathNotificationDate") val deathNotificationDate: String?,
    @SerializedName("disqualifiedPendingSentence") val disqualifiedPendingSentence: Boolean?,
    @SerializedName("eyesight") val eyesight: String?,
    @SerializedName("hearing") val hearing: String?,
    @SerializedName("imagesExist") val imagesExist: Boolean?,
    @SerializedName("isMilitary") val isMilitary: Boolean?,
    @SerializedName("approvedDrivingInstructor") val approvedDrivingInstructor: Boolean?,
    @SerializedName("retainedC1_D1Entitlement") val retainedC1D1Entitlement: Boolean?,
    @SerializedName("previousDrivingLicence") val previousDrivingLicence: List<PreviousDrivingLicence>?,
    @SerializedName("dateRevokedUntilTestPassed") val dateRevokedUntilTestPassed: String?,
    @SerializedName("penaltyPoints") val penaltyPoints: Int?,
    @SerializedName("numberOfOffences") val numberOfOffences: Int?
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
    @SerializedName("line5") val line5: String?,
    @SerializedName("postcode") val postcode: String?
)

data class PreviousDrivingLicence(
    @SerializedName("previousDrivingLicenceNumber") val previousDrivingLicenceNumber: String,
    @SerializedName("previousLastName") val previousLastName: String?,
    @SerializedName("previousFirstNames") val previousFirstNames: String?,
    @SerializedName("previousDateOfBirth") val previousDateOfBirth: String?
)

data class Licence(
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusQualifier") val statusQualifier: String?,
    @SerializedName("countryToWhichExchanged") val countryToWhichExchanged: String?
)

data class Entitlement(
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("categoryLegalLiteral") val categoryLegalLiteral: String?,
    @SerializedName("welshCategoryLegalLiteral") val welshCategoryLegalLiteral: String?,
    @SerializedName("categoryShortLiteral") val categoryShortLiteral: String?,
    @SerializedName("welshCategoryShortLiteral") val welshCategoryShortLiteral: String?,
    @SerializedName("categoryType") val categoryType: String,
    @SerializedName("fromDate") val fromDate: String?,
    @SerializedName("fromDateIsPriorTo") val fromDateIsPriorTo: Boolean?,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("categoryStatus") val categoryStatus: String,
    @SerializedName("restrictions") val restrictions: List<Restriction>?,
    @SerializedName("restrictedToAutomaticTransmission") val restrictedToAutomaticTransmission: Boolean?,
    @SerializedName("fromNonGB") val fromNonGB: Boolean?
)

data class Restriction(
    @SerializedName("restrictionCode") val restrictionCode: String,
    @SerializedName("restrictionLiteral") val restrictionLiteral: String?,
    @SerializedName("welshRestrictionLiteral") val welshRestrictionLiteral: String?
)

data class TestPass(
    @SerializedName("type") val type: String,
    @SerializedName("categoryCode") val categoryCode: String?,
    @SerializedName("categoryLegalLiteral") val categoryLegalLiteral: String?,
    @SerializedName("categoryShortLiteral") val categoryShortLiteral: String?,
    @SerializedName("testDate") val testDate: String,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("withAutomaticTransmission") val withAutomaticTransmission: Boolean?,
    @SerializedName("vehicleAdaptations") val vehicleAdaptations: List<String>?,
    @SerializedName("restrictions") val restrictions: List<Restriction>?,
    @SerializedName("withTrailer") val withTrailer: Boolean?,
    @SerializedName("extendedTest") val extendedTest: Boolean?,
    @SerializedName("licenceSurrendered") val licenceSurrendered: Boolean?,
    @SerializedName("testingAuthority") val testingAuthority: String?
)

data class Token(
    @SerializedName("type") val type: String?,
    @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String?,
    @SerializedName("issueNumber") val issueNumber: String?,
    @SerializedName("validFromDate") val validFromDate: String?,
    @SerializedName("validToDate") val validToDate: String?,
    @SerializedName("isProvisional") val isProvisional: Boolean?,
    @SerializedName("entitlements") val entitlements: List<TokenEntitlement>?
)

data class TokenEntitlement(
    @SerializedName("category") val category: String?,
    @SerializedName("categoryLegalLiteral") val categoryLegalLiteral: String?,
    @SerializedName("categoryShortLiteral") val categoryShortLiteral: String?,
    @SerializedName("categoryType") val categoryType: String?,
    @SerializedName("categoryFromDate") val categoryFromDate: String?,
    @SerializedName("categoryFromDateIsPriorTo") val categoryFromDateIsPriorTo: Boolean?,
    @SerializedName("categoryExpiryDate") val categoryExpiryDate: String?,
    @SerializedName("categoryRestrictions") val categoryRestrictions: List<TokenRestriction>?,
    @SerializedName("group") val group: String?,
    @SerializedName("groupShortLiteral") val groupShortLiteral: String?,
    @SerializedName("groupLegalLiteral") val groupLegalLiteral: String?,
    @SerializedName("groupType") val groupType: String?,
    @SerializedName("groupFromDate") val groupFromDate: String?,
    @SerializedName("groupFromDateIsPrior") val groupFromDateIsPrior: Boolean?,
    @SerializedName("groupExpiryDate") val groupExpiryDate: String?,
    @SerializedName("groupRestrictions") val groupRestrictions: List<TokenRestriction>?
)

data class TokenRestriction(
    @SerializedName("categoryRestrictionCode") val categoryRestrictionCode: String,
    @SerializedName("categoryRestrictionLiteral") val categoryRestrictionLiteral: String?,
    @SerializedName("welshCategoryRestrictionLiteral") val welshCategoryRestrictionLiteral: String?
)

data class Sdl(
    @SerializedName("tokens") val tokens: List<SdlToken>
)

data class ImageUtility(
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("signatureImageUrl") val signatureImageUrl: String?
)

data class Endorsement(
    @SerializedName("convictionCourtCode") val convictionCourtCode: String,
    @SerializedName("expiryDate") val expiryDate: String,
    @SerializedName("fromDate") val fromDate: String,
    @SerializedName("offenceCode") val offenceCode: String,
    @SerializedName("appealCourtCode") val appealCourtCode: String?,
    @SerializedName("appealCourtName") val appealCourtName: String?,
    @SerializedName("appealDate") val appealDate: String?,
    @SerializedName("convictionDate") val convictionDate: String?,
    @SerializedName("convictionCourtName") val convictionCourtName: String?,
    @SerializedName("disqualification") val disqualification: Disqualification?,
    @SerializedName("disqualificationSuspendedPendingAppealDate") val disqualificationSuspendedPendingAppealDate: String?,
    @SerializedName("disqualificationReimposedDate") val disqualificationReimposedDate: String?,
    @SerializedName("disqualificationRemovalDate") val disqualificationRemovalDate: String?,
    @SerializedName("disqualifiedPendingSentence") val disqualifiedPendingSentence: String?,
    @SerializedName("fine") val fine: Double?,
    @SerializedName("identifier") val identifier: String?,
    @SerializedName("intoxicant") val intoxicant: Intoxicant?,
    @SerializedName("markers") val markers: EndorsementMarker?,
    @SerializedName("nextReportDate") val nextReportDate: String?,
    @SerializedName("notificationSource") val notificationSource: String?,
    @SerializedName("offenceLegalLiteral") val offenceLegalLiteral: String?,
    @SerializedName("welshOffenceLegalLiteral") val welshOffenceLegalLiteral: String?,
    @SerializedName("offenceDate") val offenceDate: String?,
    @SerializedName("otherSentence") val otherSentence: String?,
    @SerializedName("otherSentenceLiteral") val otherSentenceLiteral: String?,
    @SerializedName("welshOtherSentenceLiteral") val welshOtherSentenceLiteral: String?,
    @SerializedName("penaltyPoints") val penaltyPoints: Int?,
    @SerializedName("penaltyPointsExpiryDate") val penaltyPointsExpiryDate: String?,
    @SerializedName("prisonSentSuspendedPeriod") val prisonSentSuspendedPeriod: Any?,
    @SerializedName("rehabilitationCourseCompleted") val rehabilitationCourseCompleted: Boolean?,
    @SerializedName("sentenceDate") val sentenceDate: String?,
    @SerializedName("sentencingCourtCode") val sentencingCourtCode: String?,
    @SerializedName("sentencingCourtName") val sentencingCourtName: String?
)

data class Disqualification(
    @SerializedName("type") val type: String?,
    @SerializedName("forLife") val forLife: Boolean?,
    @SerializedName("years") val years: Int?,
    @SerializedName("months") val months: Int?,
    @SerializedName("days") val days: Int?,
    @SerializedName("startDate") val startDate: String?
)

data class Intoxicant(
    @SerializedName("intoxicantType") val intoxicantType: String?,
    @SerializedName("testingMethod") val testingMethod: String?,
    @SerializedName("level") val level: Double?
)

data class EndorsementMarker(
    @SerializedName("declaredHardship") val declaredHardship: Boolean
)

data class SdlToken(
    @SerializedName("tokenId") val tokenId: String,
    @SerializedName("token") val token: String,
    @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String,
    @SerializedName("driverId") val driverId: String,
    @SerializedName("documentReference") val documentReference: String,
    @SerializedName("created") val created: String,
    @SerializedName("state") val state: String,
    @SerializedName("expiry") val expiry: String,
    @SerializedName("status") val status: String?,
    @SerializedName("redeemed") val redeemed: String?,
    @SerializedName("cancelled") val cancelled: String?
)

data class HolderDetails(
    @SerializedName("holderFirstNames") val holderFirstNames: String?,
    @SerializedName("holderSurname") val holderSurname: String?,
    @SerializedName("holderAddress") val holderAddress: Any?, // Kept generic for now to avoid 5 more nested address DTOs
    @SerializedName("holderDateOfBirth") val holderDateOfBirth: String?,
    @SerializedName("holderDrivingLicenceNumber") val holderDrivingLicenceNumber: String?,
    @SerializedName("holderDrivingLicenceIssuingNation") val holderDrivingLicenceIssuingNation: String?,
    @SerializedName("holderMarkers") val holderMarkers: List<HolderMarker>?,
    @SerializedName("tachoCards") val tachoCards: List<TachoCard>?
)

data class HolderMarker(
    @SerializedName("markerType") val markerType: String?,
    @SerializedName("deathNotificationDate") val deathNotificationDate: String?,
    @SerializedName("caseReference") val caseReference: String?
)

data class TachoCard(
    @SerializedName("cardType") val cardType: String?,
    @SerializedName("cardNumber") val cardNumber: String?,
    @SerializedName("cardStatus") val cardStatus: String?,
    @SerializedName("cardStartOfValidityDate") val cardStartOfValidityDate: String?,
    @SerializedName("cardExpiryDate") val cardExpiryDate: String?,
    @SerializedName("cardIssueDate") val cardIssueDate: String?,
    @SerializedName("cardDrivingLicenceNumber") val cardDrivingLicenceNumber: String?,
    @SerializedName("cardDLNIssuingNation") val cardDLNIssuingNation: String?,
    @SerializedName("cardSurname") val cardSurname: String?,
    @SerializedName("cardFirstNames") val cardFirstNames: String?,
    @SerializedName("cardDateOfBirth") val cardDateOfBirth: String?,
    @SerializedName("workshopName") val workshopName: String?,
    @SerializedName("workshopNumber") val workshopNumber: String?,
    @SerializedName("workshopAddress") val workshopAddress: Any?
)
