package uk.govuk.app.local.data.local.model

import io.realm.kotlin.types.RealmObject

class StoredLocalAuthorityParent: RealmObject {
    var name: String = ""
    var url: String = ""
    var slug: String = ""
}
