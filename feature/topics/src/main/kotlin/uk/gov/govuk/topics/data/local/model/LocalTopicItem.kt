package uk.gov.govuk.topics.data.local.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

internal class LocalTopicItem : RealmObject {
    @PrimaryKey
    var ref: String = ""
    var title: String = ""
    var description: String = ""
    var isSelected: Boolean = false
}