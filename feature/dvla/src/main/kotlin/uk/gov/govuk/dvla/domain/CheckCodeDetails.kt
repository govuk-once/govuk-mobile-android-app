package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.MultiShareCodeResponse
import uk.gov.govuk.dvla.remote.model.ShareCode
import uk.gov.govuk.dvla.remote.model.SingleShareCodeResponse

enum class CheckCodeValidity {
    CANCELLED,
    EXPIRED,
    VALID,
    REDEEMED,
    INVALID,
    UNKNOWN
}

enum class CheckCodeStatus {
    ACTIVE,
    INACTIVE,
    UNKNOWN
}

data class CheckCodeDetails(
    val validity: CheckCodeValidity,
    val tokenId: String,
    val token: String,
    val drivingLicenceNumber: String,
    val driverId: String,
    val documentReference: String,
    val createdAt: String,
    val expiresAt: String,
    val activationStatus: CheckCodeStatus?,
    val redeemedAt: String?,
    val cancelledAt: String?
)

fun ShareCode.toDomainModel() = CheckCodeDetails(
    validity = runCatching { CheckCodeValidity.valueOf(this.validity.name) }
        .getOrDefault(CheckCodeValidity.UNKNOWN),

    tokenId = this.tokenId,
    token = this.token,
    drivingLicenceNumber = this.drivingLicenceNumber,
    driverId = this.driverId,
    documentReference = this.documentReference,
    createdAt = this.createdAt,
    expiresAt = this.expiresAt,

    activationStatus = this.activationStatus?.let {
        runCatching { CheckCodeStatus.valueOf(it.name) }.getOrDefault(CheckCodeStatus.UNKNOWN)
    },

    redeemedAt = this.redeemedAt,
    cancelledAt = this.cancelledAt
)

fun SingleShareCodeResponse.toDomainModel(): CheckCodeDetails = this.shareCode.toDomainModel()


fun MultiShareCodeResponse.toDomainModel(): List<CheckCodeDetails> =
    this.shareCodes.map { it.toDomainModel() }
