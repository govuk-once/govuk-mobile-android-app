package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

enum class LicenceStatus {
    @SerializedName("Valid")
    VALID,

    @SerializedName("Disqualified")
    DISQUALIFIED,

    @SerializedName("Revoked")
    REVOKED,

    @SerializedName("Revoked for medical reasons")
    REVOKED_FOR_MEDICAL_REASONS,

    @SerializedName("Surrendered")
    SURRENDERED,

    @SerializedName("Surrendered voluntarily")
    SURRENDERED_VOLUNTARILY,

    @SerializedName("Surrendered for medical reasons")
    SURRENDERED_FOR_MEDICAL_REASONS,

    @SerializedName("Expired")
    EXPIRED,

    @SerializedName("Exchanged")
    EXCHANGED,

    @SerializedName("Refused")
    REFUSED,

    @SerializedName("Refused for medical reasons")
    REFUSED_FOR_MEDICAL_REASONS
}
