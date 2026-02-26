# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.gov.govuk.config.data.remote.model.Config
-keep class uk.gov.govuk.config.data.remote.model.ConfigResponse
-keep class uk.gov.govuk.config.data.remote.model.ReleaseFlags

-keep class uk.gov.govuk.config.data.remote.model.EmergencyBanner
-keep class uk.gov.govuk.config.data.remote.model.EmergencyBannerType {
    **[] values();
    ** valueOf(java.lang.String);
    <fields>;
}

-keep class uk.gov.govuk.config.data.remote.model.UserFeedbackBanner
-keep class uk.gov.govuk.config.data.remote.model.Link
-keep class uk.gov.govuk.config.data.remote.model.ChatUrls
-keep class uk.gov.govuk.config.data.remote.model.ChatBanner