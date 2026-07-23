package uk.gov.govuk.analytics

import android.app.Activity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ActivityProviderTest {
    private lateinit var activityProvider: ActivityProvider

    @Before
    fun setup() {
        activityProvider = ActivityProvider()
    }

    @Test
    fun `Given initial state, when currentActivity is accessed, then return null`() {
        assertNull(activityProvider.currentActivity)
    }

    @Test
    fun `Given an activity is created, when currentActivity is accessed, then return the activity`() {
        val activity = mockk<Activity>(relaxed = true)

        activityProvider.onActivityCreated(activity, null)

        assertEquals(activity, activityProvider.currentActivity)
    }

    @Test
    fun `Given an activity is started, when currentActivity is accessed, then return the activity`() {
        val activity = mockk<Activity>(relaxed = true)

        activityProvider.onActivityStarted(activity)

        assertEquals(activity, activityProvider.currentActivity)
    }

    @Test
    fun `Given an activity is resumed, when currentActivity is accessed, then return the activity`() {
        val activity = mockk<Activity>(relaxed = true)

        activityProvider.onActivityResumed(activity)

        assertEquals(activity, activityProvider.currentActivity)
    }

    @Test
    fun `Given a new activity is created, when currentActivity is accessed, then return the latest activity`() {
        val activity1 = mockk<Activity>(relaxed = true)
        val activity2 = mockk<Activity>(relaxed = true)

        activityProvider.onActivityCreated(activity1, null)
        activityProvider.onActivityCreated(activity2, null)

        assertEquals(activity2, activityProvider.currentActivity)
    }

    @Test
    fun `Given the current activity is destroyed, when currentActivity is accessed, then return null`() {
        val activity = mockk<Activity>(relaxed = true)

        activityProvider.onActivityCreated(activity, null)
        activityProvider.onActivityDestroyed(activity)

        assertNull(activityProvider.currentActivity)
    }

    @Test
    fun `Given an activity is destroyed that is not the current one, when currentActivity is accessed, then return the current activity`() {
        val activity1 = mockk<Activity>(relaxed = true)
        val activity2 = mockk<Activity>(relaxed = true)

        activityProvider.onActivityCreated(activity1, null)
        activityProvider.onActivityDestroyed(activity2)

        assertEquals(activity1, activityProvider.currentActivity)
    }

    @Test
    fun `Given a destroyed activity listener is registered, when an activity is destroyed, then notify the listener`() {
        val activity = mockk<Activity>(relaxed = true)
        every { activity.isChangingConfigurations } returns false
        val listener = mockk<(Activity) -> Unit>(relaxed = true)

        activityProvider.addOnActivityDestroyedListener(listener)
        activityProvider.onActivityDestroyed(activity)

        verify(exactly = 1) {
            listener(activity)
        }
    }

    @Test
    fun `Given a destroyed activity listener is registered, when an activity is destroyed due to a config change, then do not notify the listener`() {
        val activity = mockk<Activity>(relaxed = true)
        every { activity.isChangingConfigurations } returns true
        val listener = mockk<(Activity) -> Unit>(relaxed = true)

        activityProvider.addOnActivityDestroyedListener(listener)
        activityProvider.onActivityDestroyed(activity)

        verify(exactly = 0) {
            listener(activity)
        }
    }
}
