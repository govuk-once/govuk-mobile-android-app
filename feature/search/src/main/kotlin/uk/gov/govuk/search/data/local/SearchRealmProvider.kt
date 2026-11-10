package uk.gov.govuk.search.data.local

import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.data.local.RealmProvider
import uk.gov.govuk.search.data.local.model.LocalSearchItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchRealmProvider @Inject constructor(
    encryptionHelper: RealmEncryptionHelper
): RealmProvider(encryptionHelper) {

    override val schemaVersion: Long = 1
    override val name = "search"
    override val schema = setOf(LocalSearchItem::class)

}