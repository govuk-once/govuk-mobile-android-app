package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class AwardDetailData(
    @SerializedName("adjustment_amount_breakdown") val adjustmentBreakdown: List<AdjustmentBreakdown>,
    @SerializedName("adjustment_response_status") val adjustmentResponseStatus: Double,
    @SerializedName("award_amount") val awardAmount: String,
    @SerializedName("award_amount_breakdown") val awardBreakdown: List<AwardBreakdown>,
    @SerializedName("award_end_date") val awardEndDate: String,
    @SerializedName("award_name") val awardName: String,
    @SerializedName("award_start_date") val awardStartDate: String,
    @SerializedName("award_type") val awardType: String,
    @SerializedName("deduction_amount_breakdown") val deductionBreakdown: List<DeductionBreakdown>,
    @SerializedName("payment_frequency") val paymentFrequency: String
)
