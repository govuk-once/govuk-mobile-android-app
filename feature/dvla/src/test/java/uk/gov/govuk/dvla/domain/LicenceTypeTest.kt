package uk.gov.govuk.dvla.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.common.LicenceType as RemoteLicenceType

class LicenceTypeTest {
    @Test
    fun `RemoteLicenceType toDomain maps enum values correctly`() {
        assertEquals(LicenceType.PROVISIONAL, RemoteLicenceType.PROVISIONAL.toDomain())
        assertEquals(LicenceType.FULL, RemoteLicenceType.FULL.toDomain())
        val nullRemoteLicenceType: RemoteLicenceType? = null
        assertEquals(LicenceType.UNKNOWN, nullRemoteLicenceType.toDomain())
    }
}
