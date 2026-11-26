package uk.gov.govuk.search.data.local.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// Kept for Realm → Room data migration only. Remove in Phase 5 cleanup.
internal class LocalSearchItem : RealmObject {
    @PrimaryKey
    var searchTerm: String = ""
    var timestamp: Long = 0
}
