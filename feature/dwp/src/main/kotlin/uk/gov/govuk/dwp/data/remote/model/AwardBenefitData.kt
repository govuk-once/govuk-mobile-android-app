package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class AwardBenefitData(
    @SerializedName("data") val data: AwardDetailData
)
