package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName

data class CustomerSummaryResponse(
    @SerializedName("linkingId") val linkingId: String,
    @SerializedName("customerResponse") val customerResponse: CustomerResponse,
    @SerializedName("driversEligibilityResponse") val driversEligibilityResponse: DriversEligibilityResponse,
    @SerializedName("driversSuppressionResponse") val driversSuppressionResponse: DriversSuppressionResponse,
    @SerializedName("applicationTaskResponse") val applicationTaskResponse: Any?,
    @SerializedName("vehicleResponse") val vehicleResponse: List<Any>,
    @SerializedName("hasErrors") val hasErrors: Boolean
)

data class CustomerResponse(
    @SerializedName("customer") val customer: Customer
)

data class Customer(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerNumber") val customerNumber: String?,
    @SerializedName("identityId") val identityId: String,
    @SerializedName("recordStatus") val recordStatus: String,
    @SerializedName("customerType") val customerType: String,
    @SerializedName("address") val address: Any?,
    @SerializedName("emailAddress") val emailAddress: String,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("individualDetails") val individualDetails: IndividualDetails,
    @SerializedName("products") val products: List<Product>,
    @SerializedName("contactPreferences") val contactPreferences: List<ContactPreference>,
    @SerializedName("applications") val applications: List<Any>,
    @SerializedName("suppressions") val suppressions: List<Any>,
    @SerializedName("languagePreference") val languagePreference: String,
    @SerializedName("lastAction") val lastAction: Any?,
    @SerializedName("cases") val cases: List<Any>
)

data class IndividualDetails(
    @SerializedName("title") val title: String,
    @SerializedName("firstNames") val firstNames: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("notifiedOfDeath") val notifiedOfDeath: String?
)

data class Product(
    @SerializedName("productType") val productType: String,
    @SerializedName("productKey") val productKey: String,
    @SerializedName("productIdentifier") val productIdentifier: String,
    @SerializedName("productSummary") val productSummary: Any?,
    @SerializedName("dateAdded") val dateAdded: String
)

data class ContactPreference(
    @SerializedName("contactType") val contactType: String,
    @SerializedName("contactPreference") val contactPreference: String,
    @SerializedName("consentDate") val consentDate: String
)

data class DriversSuppressionResponse(
    @SerializedName("drivingLicenceNumber") val drivingLicenceNumber: String,
    @SerializedName("suppressionStatus") val suppressionStatus: List<SuppressionStatus>
)

data class SuppressionStatus(
    @SerializedName("role") val role: String,
    @SerializedName("suppressed") val suppressed: Boolean
)

// TODO: these exist in DriverSummaryResponse too possibly

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