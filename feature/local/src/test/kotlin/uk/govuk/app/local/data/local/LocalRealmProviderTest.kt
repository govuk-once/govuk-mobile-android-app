package uk.govuk.app.local.data.local

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
import uk.govuk.app.local.data.local.model.StoredLocalAuthorityParent

class LocalRealmProviderTest {
    private val encryptionHelper = mockk<RealmEncryptionHelper>()
    private val provider = LocalRealmProvider(encryptionHelper)

    @Test
    fun `Verify schema version`() {
        assertEquals(1L, provider.schemaVersion)
    }

    @Test
    fun `Verify database name`() {
        assertEquals("local", provider.name)
    }

    @Test
    fun `Verify schema contains expected classes`() {
        val expected = setOf(
            StoredLocalAuthority::class,
            StoredLocalAuthorityParent::class
        )
        assertEquals(expected, provider.schema)
    }
}
