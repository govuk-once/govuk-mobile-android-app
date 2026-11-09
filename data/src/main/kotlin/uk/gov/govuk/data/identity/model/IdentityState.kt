package uk.gov.govuk.data.identity.model

sealed interface IdentityState {
    data object Checking: IdentityState
    data class Success(val services: List<LinkedService>) : IdentityState
    data object Error : IdentityState
}