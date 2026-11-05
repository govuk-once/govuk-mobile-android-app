package uk.gov.govuk.search.data.local.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

internal class LocalSearchItem : RealmObject {
    @PrimaryKey
    var searchTerm: String = ""
    var timestamp: Long = 0
}