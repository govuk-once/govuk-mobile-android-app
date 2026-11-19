package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class PaymentsQueryData(
    @SerializedName("deductionamount") val deductionAmount: String,
    @SerializedName("awardamount") val awardAmount: String,
    @SerializedName("benefitName") val benefitName: String,
    @SerializedName("benefitShortName") val benefitShortName: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("status") val status: String,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("translatedDueDate") val translatedDueDate: String
)
