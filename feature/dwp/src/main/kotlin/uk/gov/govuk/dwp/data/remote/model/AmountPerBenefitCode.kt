package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class AmountPerBenefitCode(
    @SerializedName("benefitShortName") val benefitShortName: String,
    @SerializedName("benefitName") val benefitName: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("benefitCode") val benefitCode: Int
)
