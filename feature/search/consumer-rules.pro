# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.gov.govuk.search.data.remote.model.AutocompleteResponse
-keep class uk.gov.govuk.search.data.remote.model.SearchResponse
-keep class uk.gov.govuk.search.data.remote.model.SearchResult
