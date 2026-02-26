package uk.gov.govuk.topics.data.local

import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.data.local.RealmProvider
import uk.gov.govuk.topics.data.local.model.LocalTopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsRealmProvider @Inject constructor(
    encryptionHelper: RealmEncryptionHelper
): RealmProvider(encryptionHelper) {

    override val schemaVersion: Long = 1
    override val name = "topics"
    override val schema = setOf(LocalTopicItem::class)

}