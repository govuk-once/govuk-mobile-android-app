package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.CustomerSummaryResponse

// TODO: this is to demonstrate the endpoint call data, until we decide which endpoint to use
data class CustomerSummaryDetails(
    val customerId: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val emailAddress: String?,
    val recordStatus: String
)

fun CustomerSummaryResponse.toDomainModel(): CustomerSummaryDetails {
    val customer = this.customerResponse.customer
    val individualDetails = customer.individualDetails

    return CustomerSummaryDetails(
        customerId = customer.customerId,
        lastName = individualDetails.lastName,
        dateOfBirth = individualDetails.dateOfBirth,
        recordStatus = customer.recordStatus,
        firstName = individualDetails.firstNames ?: "",
        emailAddress = customer.emailAddress
    )
}