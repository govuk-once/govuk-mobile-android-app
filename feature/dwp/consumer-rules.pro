# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.gov.govuk.dwp.data.remote.model.AdjustmentBreakdown
-keep class uk.gov.govuk.dwp.data.remote.model.AmountPerBenefitCode
-keep class uk.gov.govuk.dwp.data.remote.model.Award
-keep class uk.gov.govuk.dwp.data.remote.model.AwardBenefit
-keep class uk.gov.govuk.dwp.data.remote.model.AwardBenefitData
-keep class uk.gov.govuk.dwp.data.remote.model.AwardBreakdown
-keep class uk.gov.govuk.dwp.data.remote.model.AwardData
-keep class uk.gov.govuk.dwp.data.remote.model.AwardDetailData
-keep class uk.gov.govuk.dwp.data.remote.model.AwardDetailsResponse
-keep class uk.gov.govuk.dwp.data.remote.model.BioData
-keep class uk.gov.govuk.dwp.data.remote.model.CoreBioResponse
-keep class uk.gov.govuk.dwp.data.remote.model.DeductionBreakdown
-keep class uk.gov.govuk.dwp.data.remote.model.Payment
-keep class uk.gov.govuk.dwp.data.remote.model.PaymentData
-keep class uk.gov.govuk.dwp.data.remote.model.PaymentsQueryData
-keep class uk.gov.govuk.dwp.data.remote.model.PaymentsQueryResponse
