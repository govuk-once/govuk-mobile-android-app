# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.gov.govuk.travelalerts.data.model.Country { <fields>; }
-keep class uk.gov.govuk.travelalerts.data.model.Group
-keep class uk.gov.govuk.travelalerts.data.model.SubscriptionRequest
-keep class uk.gov.govuk.travelalerts.data.model.SubscriptionRequest$Action
-keep class uk.gov.govuk.travelalerts.data.model.Subgroup { **[] values(); ** valueOf(java.lang.String); <fields>; }