package uk.govuk.app.local.data.remote.model

internal sealed class LocalAuthorityResult {
    data class LocalAuthority(val localAuthority: RemoteLocalAuthority): LocalAuthorityResult()
    data class Addresses(val addresses: List<RemoteAddress>): LocalAuthorityResult()
    data object InvalidPostcode: LocalAuthorityResult()
    data object PostcodeNotFound: LocalAuthorityResult()
    data object PostcodeEmptyOrNull: LocalAuthorityResult()
    data object ApiNotResponding: LocalAuthorityResult()
    data object DeviceNotConnected: LocalAuthorityResult()
}
