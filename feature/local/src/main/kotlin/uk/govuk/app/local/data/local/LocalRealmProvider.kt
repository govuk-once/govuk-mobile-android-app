package uk.govuk.app.local.data.local

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.data.local.RealmProvider
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
import uk.govuk.app.local.data.local.model.StoredLocalAuthorityParent

@Singleton
internal class LocalRealmProvider @Inject constructor(
    encryptionHelper: RealmEncryptionHelper
) : RealmProvider(encryptionHelper) {
    override val schemaVersion: Long = 1
    override val name = "local"
    override val schema = setOf(StoredLocalAuthority::class, StoredLocalAuthorityParent::class)
}
