package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class PaymentData(
    @SerializedName("benefitCodes") val benefitCodes: List<Int>,
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("statusDescription") val statusDescription: String,
    @SerializedName("netAmount") val netAmount: Double,
    @SerializedName("issueDate") val issueDate: String,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("deductionValue") val deductionValue: Double,
    @SerializedName("amount") val amount: Double,
    @SerializedName("translatedDueDate") val translatedDueDate: String,
    @SerializedName("amountsPerBenefitCode") val amountsPerBenefitCode: List<AmountPerBenefitCode>
)
