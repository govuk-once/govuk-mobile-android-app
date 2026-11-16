package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class AwardBenefit(
    @SerializedName("benefit") val benefit: AwardBenefitData
)
