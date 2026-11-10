package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.model.Result

internal sealed interface LicenceDetailsResult {
    data class Success(val details: LicenceDetails) : LicenceDetailsResult

    // GUK-404-04 "Driving Licence not found"
    data object NotFound : LicenceDetailsResult

    // GUK-404-05 "Driving licence not available for enquiry"
    data object NotAvailableForEnquiry : LicenceDetailsResult

    data class Failure(val result: Result<LicenceDetails>) : LicenceDetailsResult
}
