package uk.gov.govuk.data.identity.model

/** Services the app supports linking to.
 * Add any new services here.
 * serviceName used is the name Flex defines the service as. */
enum class LinkedService(val serviceName: String) {
    DVLA("dvla"),
}