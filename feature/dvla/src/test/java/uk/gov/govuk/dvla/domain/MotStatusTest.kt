package uk.gov.govuk.dvla.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus

class MotStatusTest {
    @Test
    fun `Verify a null status is UNKNOWN`() {
        val remote: RemoteMotStatus? = null
        assertEquals(MotStatus.UNKNOWN, remote.toDomain())
    }

    @Test
    fun `Verify VALID status is VALID`() {
        val remote = RemoteMotStatus.VALID
        assertEquals(MotStatus.VALID, remote.toDomain())
    }

    @Test
    fun `Verify NOT_VALID status is EXPIRED`() {
        val remote = RemoteMotStatus.NOT_VALID
        assertEquals(MotStatus.EXPIRED, remote.toDomain())
    }

    @Test
    fun `Verify NO_DETAILS_HELD status is UNKNOWN`() {
        val remote = RemoteMotStatus.NO_DETAILS_HELD
        assertEquals(MotStatus.UNKNOWN, remote.toDomain())
    }

    @Test
    fun `Verify NO_RESULTS_RETURNED status is UNKNOWN`() {
        val remote = RemoteMotStatus.NO_RESULTS_RETURNED
        assertEquals(MotStatus.UNKNOWN, remote.toDomain())
    }
}
