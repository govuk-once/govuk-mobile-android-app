package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class AwardData(
    @SerializedName("ame_last_issued") val lastIssued: Payment,
    @SerializedName("ame_upcoming") val upcoming: Payment,
    @SerializedName("sas_award_details") val awardDetail: AwardBenefit
)
