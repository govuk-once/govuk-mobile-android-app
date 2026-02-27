package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

enum class ConsentStatus() {
    @SerializedName("accepted")
    ACCEPTED,
    @SerializedName("denied")
    DENIED,
    @SerializedName("unknown")
    UNKNOWN
}
