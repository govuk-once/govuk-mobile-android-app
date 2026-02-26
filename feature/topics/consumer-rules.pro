# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.gov.govuk.topics.data.remote.model.RemoteTopicItem
-keep class uk.gov.govuk.topics.data.remote.model.RemoteTopic
-keep class uk.gov.govuk.topics.data.remote.model.RemoteTopic$RemoteTopicContent
