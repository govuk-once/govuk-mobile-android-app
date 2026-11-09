package uk.gov.govuk.analytics;

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FirebaseIdentifiersTest {
    private val firebaseAnalytics = mockk<FirebaseAnalytics>()
    private val appInstanceIdTask = mockk<Task<String>>()
    private val sessionIdTask = mockk<Task<Long?>>()
    private lateinit var firebaseIdentifiers: FirebaseIdentifiers

    @Before
    fun setup() {
        every { firebaseAnalytics.appInstanceId } returns appInstanceIdTask
        every { appInstanceIdTask.addOnCompleteListener(any()) } returns appInstanceIdTask

        every { firebaseAnalytics.sessionId } returns sessionIdTask
        every { sessionIdTask.addOnCompleteListener(any()) } returns sessionIdTask

        firebaseIdentifiers = FirebaseIdentifiers(firebaseAnalytics)
    }

    @Test
    fun `Given an initial state, then the identifiers are null`() {
        assertNull(firebaseIdentifiers.userPseudoId)
        assertNull(firebaseIdentifiers.sessionId)
    }

    @Test
    fun `Given a refresh, when both tasks succeed, then the identifiers are cached`() {
        val userIdListenerSlot = slot<OnCompleteListener<String>>()
        every { appInstanceIdTask.isSuccessful } returns true
        every { appInstanceIdTask.result } returns "pseudo_id"
        every { appInstanceIdTask.addOnCompleteListener(capture(userIdListenerSlot)) } returns appInstanceIdTask

        val sessionListenerSlot = slot<OnCompleteListener<Long?>>()
        every { sessionIdTask.isSuccessful } returns true
        every { sessionIdTask.result } returns 42L
        every { sessionIdTask.addOnCompleteListener(capture(sessionListenerSlot)) } returns sessionIdTask

        firebaseIdentifiers.refresh()
        userIdListenerSlot.captured.onComplete(appInstanceIdTask)
        sessionListenerSlot.captured.onComplete(sessionIdTask)

        assertEquals("pseudo_id", firebaseIdentifiers.userPseudoId)
        assertEquals("42", firebaseIdentifiers.sessionId)
    }

    @Test
    fun `Given a refresh, when tasks fail, then cached identifiers are kept`() {
        val userIdListenerSlot = slot<OnCompleteListener<String>>()
        every { appInstanceIdTask.isSuccessful } returns false
        every { appInstanceIdTask.addOnCompleteListener(capture(userIdListenerSlot)) } returns appInstanceIdTask

        val sessionListenerSlot = slot<OnCompleteListener<Long?>>()
        every { sessionIdTask.isSuccessful } returns false
        every { sessionIdTask.addOnCompleteListener(capture(sessionListenerSlot)) } returns sessionIdTask

        firebaseIdentifiers.refresh()
        userIdListenerSlot.captured.onComplete(appInstanceIdTask)
        sessionListenerSlot.captured.onComplete(sessionIdTask)

        assertNull(firebaseIdentifiers.userPseudoId)
        assertNull(firebaseIdentifiers.sessionId)
    }
}
