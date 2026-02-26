package uk.gov.govuk.data.auth

interface AttestationProvider {

    suspend fun getToken(): String?

}