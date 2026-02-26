package uk.gov.govuk.visited.data.store

import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.data.local.RealmProvider
import uk.gov.govuk.visited.data.model.VisitedItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedRealmProvider @Inject constructor(
    encryptionHelper: RealmEncryptionHelper
): RealmProvider(encryptionHelper) {

    override val schemaVersion: Long = 1
    override val name = "visited"
    override val schema = setOf(VisitedItem::class)

}
