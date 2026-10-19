package uk.gov.govuk.dvla.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.common.LicenceStatus as RemoteLicenceStatus

class LicenceStatusTest {
    @Test
    fun `RemoteLicenceStatus toDomain maps enum values correctly`() {
        assertEquals(LicenceStatus.VALID, RemoteLicenceStatus.VALID.toDomain())
        assertEquals(LicenceStatus.DISQUALIFIED, RemoteLicenceStatus.DISQUALIFIED.toDomain())
        assertEquals(LicenceStatus.REVOKED, RemoteLicenceStatus.REVOKED.toDomain())
        assertEquals(LicenceStatus.REVOKED_FOR_MEDICAL_REASONS, RemoteLicenceStatus.REVOKED_FOR_MEDICAL_REASONS.toDomain())
        assertEquals(LicenceStatus.SURRENDERED, RemoteLicenceStatus.SURRENDERED.toDomain())
        assertEquals(LicenceStatus.SURRENDERED_VOLUNTARILY, RemoteLicenceStatus.SURRENDERED_VOLUNTARILY.toDomain())
        assertEquals(LicenceStatus.SURRENDERED_FOR_MEDICAL_REASONS, RemoteLicenceStatus.SURRENDERED_FOR_MEDICAL_REASONS.toDomain())
        assertEquals(LicenceStatus.EXPIRED, RemoteLicenceStatus.EXPIRED.toDomain())
        assertEquals(LicenceStatus.EXCHANGED, RemoteLicenceStatus.EXCHANGED.toDomain())
        assertEquals(LicenceStatus.REFUSED, RemoteLicenceStatus.REFUSED.toDomain())
        assertEquals(LicenceStatus.REFUSED_FOR_MEDICAL_REASONS, RemoteLicenceStatus.REFUSED_FOR_MEDICAL_REASONS.toDomain())
    }
}
