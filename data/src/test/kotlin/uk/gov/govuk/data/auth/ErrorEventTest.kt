package uk.gov.govuk.data.auth

import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorEventTest {
    @Test
    fun `Verify ErrorEvent data objects equality`() {
        assertEquals(ErrorEvent.UnableToSignInError, ErrorEvent.UnableToSignInError)
        assertEquals(ErrorEvent.UnableToSignOutError, ErrorEvent.UnableToSignOutError)
        assertEquals(ErrorEvent.UserApiError, ErrorEvent.UserApiError)
    }

    @Test
    fun `Verify ErrorEvent toString values`() {
        assertEquals("UnableToSignInError", ErrorEvent.UnableToSignInError.toString())
        assertEquals("UnableToSignOutError", ErrorEvent.UnableToSignOutError.toString())
        assertEquals("UserApiError", ErrorEvent.UserApiError.toString())
    }
}
