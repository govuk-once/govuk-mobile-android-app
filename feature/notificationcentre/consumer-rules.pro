# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.gov.govuk.chat.data.remote.model.Answer
-keep class uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
-keep class uk.gov.govuk.chat.data.remote.model.Conversation
-keep class uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest
-keep class uk.gov.govuk.chat.data.remote.model.Source
