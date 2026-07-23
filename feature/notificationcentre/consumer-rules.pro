# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# data/model/Notification.kt
-keep class uk.gov.govuk.notificationcentre.data.model.Notification

# data/model/UpdateNotificationRequestBody.kt
-keep class uk.gov.govuk.notificationcentre.data.model.UpdateNotificationRequestBody

