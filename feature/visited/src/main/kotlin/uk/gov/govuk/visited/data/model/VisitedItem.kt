package uk.gov.govuk.visited.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class VisitedItem: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var title: String = ""
    var url: String = ""
    var lastVisited: Long = 0
}
