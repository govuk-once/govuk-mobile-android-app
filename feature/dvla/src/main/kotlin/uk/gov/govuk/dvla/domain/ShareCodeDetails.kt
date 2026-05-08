package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.MultiShareCodeResponse
import uk.gov.govuk.dvla.remote.model.ShareCode
import uk.gov.govuk.dvla.remote.model.SingleShareCodeResponse

enum class ShareCodeValidity {
    CANCELLED,
    EXPIRED,
    VALID,
    REDEEMED,
    INVALID,
    UNKNOWN
}

enum class ShareCodeStatus {
    ACTIVE,
    INACTIVE,
    UNKNOWN
}

data class ShareCodeDetails(
    val validity: ShareCodeValidity,
    val tokenId: String,
    val token: String,
    val drivingLicenceNumber: String,
    val driverId: String,
    val documentReference: String,
    val createdAt: String,
    val expiresAt: String,
    val activationStatus: ShareCodeStatus?,
    val redeemedAt: String?,
    val cancelledAt: String?
)

fun ShareCode.toDomainModel() = ShareCodeDetails(
    validity = runCatching { ShareCodeValidity.valueOf(this.validity.name) }
        .getOrDefault(ShareCodeValidity.UNKNOWN),

    tokenId = this.tokenId,
    token = this.token,
    drivingLicenceNumber = this.drivingLicenceNumber,
    driverId = this.driverId,
    documentReference = this.documentReference,
    createdAt = this.createdAt,
    expiresAt = this.expiresAt,

    activationStatus = this.activationStatus?.let {
        runCatching { ShareCodeStatus.valueOf(it.name) }.getOrDefault(ShareCodeStatus.UNKNOWN)
    },

    redeemedAt = this.redeemedAt,
    cancelledAt = this.cancelledAt
)

fun SingleShareCodeResponse.toDomainModel(): ShareCodeDetails = this.shareCode.toDomainModel()


fun MultiShareCodeResponse.toDomainModel(): List<ShareCodeDetails> =
    this.shareCodes.map { it.toDomainModel() }
