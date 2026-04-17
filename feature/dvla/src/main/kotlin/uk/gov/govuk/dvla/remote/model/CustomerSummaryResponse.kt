package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.remote.model.common.DriversEligibility

data class CustomerSummaryResponse(
    @SerializedName("linkingId") val linkingId: String,
    @SerializedName("customerResponse") val customerResponse: CustomerResponse,
    @SerializedName("driversEligibilityResponse") val driversEligibility: DriversEligibility?,
    @SerializedName("driversSuppressionResponse") val driversSuppression: DriversSuppression?,
    @SerializedName("applicationTaskResponse") val applicationTaskResponse: ApplicationTaskResponse?,
    @SerializedName("vehicleResponse") val vehicleResponse: List<Vehicle>?,
    @SerializedName("hasErrors") val hasErrors: Boolean
)

data class CustomerResponse(
    @SerializedName("customer") val customer: Customer
)

data class Customer(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerNumber") val customerNumber: String?,
    @SerializedName("identityId") val identityId: String?,
    @SerializedName("recordStatus") val recordStatus: String,
    @SerializedName("customerType") val customerType: String,
    @SerializedName("address") val address: Any?, // kept generic to avoid address explosion
    @SerializedName("emailAddress") val emailAddress: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("individualDetails") val individualDetails: IndividualDetails,
    @SerializedName("products") val products: List<CustomerProduct>?,
    @SerializedName("contactPreferences") val contactPreferences: List<ContactPreference>?,
    @SerializedName("applications") val applications: List<CustomerApplication>?,
    @SerializedName("suppressions") val suppressions: List<CustomerSuppression>,
    @SerializedName("languagePreference") val languagePreference: String?,
    @SerializedName("lastAction") val lastAction: LastAction?,
    @SerializedName("cases") val cases: List<Case>?
)

data class IndividualDetails(
    @SerializedName("title") val title: String?,
    @SerializedName("firstNames") val firstNames: String?,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("notifiedOfDeath") val notifiedOfDeath: NotifiedOfDeath?
)

data class NotifiedOfDeath(
    @SerializedName("notificationType") val notificationType: String,
    @SerializedName("notificationDate") val notificationDate: String
)

data class CustomerProduct(
    @SerializedName("productType") val productType: String,
    @SerializedName("productKey") val productKey: String,
    @SerializedName("productIdentifier") val productIdentifier: String,
    @SerializedName("productSummary") val productSummary: Any?, // kept generic for now due to nested structure
    @SerializedName("dateAdded") val dateAdded: String
)

data class ContactPreference(
    @SerializedName("contactType") val contactType: String,
    @SerializedName("contactPreference") val contactPreference: String,
    @SerializedName("consentDate") val consentDate: String
)

data class CustomerApplication(
    @SerializedName("applicationType") val applicationType: String,
    @SerializedName("applicationKey") val applicationKey: String,
    @SerializedName("applicationId") val applicationId: String,
    @SerializedName("applicationState") val applicationState: ApplicationState,
    @SerializedName("transactionOutcome") val transactionOutcome: String?,
    @SerializedName("timeAdded") val timeAdded: String
)

data class ApplicationState(
    @SerializedName("status") val status: String,
    @SerializedName("reason") val reason: String?,
    @SerializedName("timeUpdated") val timeUpdated: String
)

data class CustomerSuppression(
    @SerializedName("suppressionType") val suppressionType: String,
    @SerializedName("suppressionDate") val suppressionDate: String
)

data class LastAction(
    @SerializedName("id") val id: String,
    @SerializedName("level") val level: String?,
    @SerializedName("source") val source: String,
    @SerializedName("specVersion") val specVersion: String,
    @SerializedName("type") val type: String,
    @SerializedName("dataContentType") val dataContentType: String,
    @SerializedName("dataSchema") val dataSchema: String,
    @SerializedName("time") val time: String,
    @SerializedName("data") val data: Any?, // varies drastically by event
    @SerializedName("metadata") val metadata: ActionMetadata
)

data class ActionMetadata(
    @SerializedName("environment") val environment: String,
    @SerializedName("technicalProductName") val technicalProductName: String?,
    @SerializedName("businessServiceName") val businessServiceName: String?,
    @SerializedName("handler") val handler: ActionHandler,
    @SerializedName("correlationId") val correlationId: String,
    @SerializedName("dvlaSessionId") val dvlaSessionId: String?,
    @SerializedName("requestId") val requestId: String?,
    @SerializedName("origin") val origin: ActionOrigin,
    @SerializedName("continuationToken") val continuationToken: String?,
    @SerializedName("idempotencyKey") val idempotencyKey: String?,
    @SerializedName("businessIdentifiers") val businessIdentifiers: Any? // kept generic for now to avoid nested explosion
)

data class ActionHandler(
    @SerializedName("urn") val urn: String
)

data class ActionOrigin(
    @SerializedName("urn") val urn: String,
    @SerializedName("user") val user: String?
)

data class Case(
    @SerializedName("caseId") val caseId: String,
    @SerializedName("caseKey") val caseKey: String,
    @SerializedName("caseType") val caseType: String,
    @SerializedName("caseStatus") val caseStatus: String,
    @SerializedName("caseStatusDate") val caseStatusDate: String,
    @SerializedName("applicationId") val applicationId: String?
)

data class DriversSuppression(
    @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String,
    @SerializedName("suppressionStatus") val suppressionStatus: List<SuppressionStatus>
)

data class SuppressionStatus(
    @SerializedName("role") val role: String,
    @SerializedName("suppressed") val suppressed: Boolean
)

data class ApplicationTaskResponse(
    @SerializedName("tasks") val tasks: List<ApplicationTask>
)

data class ApplicationTask(
    @SerializedName("applicationId") val applicationId: String,
    @SerializedName("taskId") val taskId: String,
    @SerializedName("subjectId") val subjectId: String,
    @SerializedName("subjectType") val subjectType: String,
    @SerializedName("taskStatus") val taskStatus: String,
    @SerializedName("taskTarget") val taskTarget: String,
    @SerializedName("taskType") val taskType: String,
    @SerializedName("version") val version: Int?
)

data class Vehicle(
    @SerializedName("registrationNumber") val registrationNumber: String,
    @SerializedName("recordType") val recordType: String?,
    @SerializedName("vehicleId") val vehicleId: Int,
    @SerializedName("chassisVin") val chassisVin: String,
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String,
    @SerializedName("manufacturerVehicleType") val manufacturerVehicleType: String,
    @SerializedName("typeApprovalVariant") val typeApprovalVariant: String,
    @SerializedName("typeApprovalVersion") val typeApprovalVersion: String,
    @SerializedName("typeApprovalCategory") val typeApprovalCategory: String,
    @SerializedName("typeApprovalNumber") val typeApprovalNumber: String?,
    @SerializedName("engineNumber") val engineNumber: String,
    @SerializedName("euroStatus") val euroStatus: String,
    @SerializedName("dateOfManufacture") val dateOfManufacture: String?,
    @SerializedName("dateOfFirstRegistration") val dateOfFirstRegistration: String,
    @SerializedName("dateOfFirstDvlaRegistration") val dateOfFirstDvlaRegistration: String?,
    @SerializedName("dateScrapped") val dateScrapped: String?,
    @SerializedName("dateDestroyed") val dateDestroyed: String?,
    @SerializedName("dateExported") val dateExported: String?,
    @SerializedName("dateStolen") val dateStolen: String?,
    @SerializedName("taxedUntil") val taxedUntil: String?,
    @SerializedName("registrationDocumentId") val registrationDocumentId: String?,
    @SerializedName("registrationDocumentIssueDate") val registrationDocumentIssueDate: String?,
    @SerializedName("engineCapacity") val engineCapacity: Int?,
    @SerializedName("maxNetPower") val maxNetPower: Int,
    @SerializedName("bodyType") val bodyType: String,
    @SerializedName("seatingCapacity") val seatingCapacity: Int,
    @SerializedName("standingCapacity") val standingCapacity: Int,
    @SerializedName("autonomousVehicle") val autonomousVehicle: Boolean,
    @SerializedName("dateOfLiability") val dateOfLiability: String?,
    @SerializedName("sornStart") val sornStart: String?,
    @SerializedName("taxClass") val taxClass: String,
    @SerializedName("taxStatus") val taxStatus: String?,
    @SerializedName("artEndDate") val artEndDate: String?,
    @SerializedName("motExpiryDate") val motExpiryDate: String?,
    @SerializedName("motStatus") val motStatus: String,
    @SerializedName("colour") val colour: String,
    @SerializedName("secondaryColour") val secondaryColour: String?,
    @SerializedName("fuelType") val fuelType: String,
    @SerializedName("wheelplan") val wheelplan: String,
    @SerializedName("revenueWeight") val revenueWeight: Int?,
    @SerializedName("massInService") val massInService: Int?,
    @SerializedName("maxPermissibleMass") val maxPermissibleMass: Int,
    @SerializedName("maxTowableTrailerMass") val maxTowableTrailerMass: MaxTowableTrailerMass?,
    @SerializedName("powerToWeightRatio") val powerToWeightRatio: Double,
    @SerializedName("roadFriendlySuspensionApplied") val roadFriendlySuspensionApplied: Boolean,
    @SerializedName("realDrivingEmissions") val realDrivingEmissions: String,
    @SerializedName("soundLevel") val soundLevel: SoundLevel?,
    @SerializedName("currentLicence") val currentLicence: CurrentLicence?,
    @SerializedName("exhaustEmissions") val exhaustEmissions: ExhaustEmissions?,
    @SerializedName("numberOfPreviousKeepers") val numberOfPreviousKeepers: Int,
    @SerializedName("keeper") val keeper: VehicleKeeper?,
    @SerializedName("taxRates") val taxRates: List<TaxRate>?
)

data class MaxTowableTrailerMass(
    @SerializedName("braked") val braked: Int?,
    @SerializedName("unbraked") val unbraked: Int?
)

data class SoundLevel(
    @SerializedName("driveBy") val driveBy: Int?,
    @SerializedName("engineSpeed") val engineSpeed: Int?,
    @SerializedName("stationary") val stationary: Int?
)

data class CurrentLicence(
    @SerializedName("period") val period: Int?,
    @SerializedName("paymentMethod") val paymentMethod: String?
)

data class ExhaustEmissions(
    @SerializedName("co2") val co2: Int?,
    @SerializedName("co") val co: Double?,
    @SerializedName("nox") val nox: Double?,
    @SerializedName("hc") val hc: Double?,
    @SerializedName("hcNox") val hcNox: Double?,
    @SerializedName("particulates") val particulates: Double?
)

data class VehicleKeeper(
    @SerializedName("companyName") val companyName: String?,
    @SerializedName("fleetNumber") val fleetNumber: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("firstNames") val firstNames: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("start") val start: String?,
    @SerializedName("inTrade") val inTrade: Boolean?,
    @SerializedName("sensitiveKeeper") val sensitiveKeeper: Boolean?,
    @SerializedName("address") val address: Any? // kept generic for now to avoid address explosion
)

data class TaxRate(
    @SerializedName("description") val description: String?,
    @SerializedName("vedBand") val vedBand: String?,
    @SerializedName("rate12Months") val rate12Months: Double?,
    @SerializedName("rate06Months") val rate06Months: Double?,
    @SerializedName("rate12MonthsDDSingle") val rate12MonthsDDSingle: Double?,
    @SerializedName("rate06MonthsDDSingle") val rate06MonthsDDSingle: Double?,
    @SerializedName("rate12MonthsDDMonthlyTotal") val rate12MonthsDDMonthlyTotal: Double?,
    @SerializedName("rate12MonthsDDMonthly") val rate12MonthsDDMonthly: Double?,
    @SerializedName("proRataArtVedMonths") val proRataArtVedMonths: Int?,
    @SerializedName("proRataArtVedTotal") val proRataArtVedTotal: Double?,
    @SerializedName("proRataArtVedMonthly") val proRataArtVedMonthly: Double?,
    @SerializedName("proRataVedMonths") val proRataVedMonths: Int?,
    @SerializedName("proRataVedTotal") val proRataVedTotal: Double?,
    @SerializedName("proRataVedMonthly") val proRataVedMonthly: Double?,
    @SerializedName("levyBand") val levyBand: String?,
    @SerializedName("hgvAxleConfiguration") val hgvAxleConfiguration: String?,
    @SerializedName("hasError") val hasError: Boolean?,
    @SerializedName("errorCode") val errorCode: String?
)