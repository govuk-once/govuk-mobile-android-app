# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.govuk.app.local.data.remote.model.RemoteAddress
-keep class uk.govuk.app.local.data.remote.model.LocalAuthorityResponse
-keep class uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
