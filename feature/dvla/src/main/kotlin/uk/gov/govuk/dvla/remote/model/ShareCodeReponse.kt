package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName

data class SingleShareCodeResponse(
    @SerializedName("linkingId")
    val linkingId: String,

    @SerializedName("shareCode")
    val shareCode: ShareCode
)

data class MultiShareCodeResponse(
    @SerializedName("linkingId")
    val linkingId: String,

    @SerializedName("shareCodes")
    val shareCodes: List<ShareCode>
)

data class ShareCode(
    @SerializedName("state")
    val validity: ShareCodeValidity,

    @SerializedName("tokenId")
    val tokenId: String,

    @SerializedName("token")
    val token: String,

    @SerializedName("drivingLicenceNumber")
    val drivingLicenceNumber: String,

    @SerializedName("driverId")
    val driverId: String,

    @SerializedName("documentReference")
    val documentReference: String,

    @SerializedName("created")
    val createdAt: String, // ISO 8601 YYYY-MM-DDTHH:mm:ss.SSSZ

    @SerializedName("expiry")
    val expiresAt: String, // ISO 8601 YYYY-MM-DDTHH:mm:ss.SSSZ

    @SerializedName("status")
    val activationStatus: ShareCodeActivationStatus?,

    @SerializedName("redeemed")
    val redeemedAt: String?, // ISO 8601 YYYY-MM-DDTHH:mm:ss.SSSZ

    @SerializedName("cancelled")
    val cancelledAt: String? // ISO 8601 YYYY-MM-DDTHH:mm:ss.SSSZ
)

enum class ShareCodeValidity {
    @SerializedName("cancelled") CANCELLED,
    @SerializedName("expired") EXPIRED,
    @SerializedName("valid") VALID,
    @SerializedName("redeemed") REDEEMED,
    @SerializedName("invalid") INVALID
}

enum class ShareCodeActivationStatus {
    @SerializedName("active") ACTIVE,
    @SerializedName("inactive") INACTIVE
}