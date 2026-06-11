package uk.gov.govuk.analytics

import android.app.Activity
import io.mockk.mockk
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
        val activity = mockk<Activity>()

        activityProvider.onActivityCreated(activity, null)

        assertEquals(activity, activityProvider.currentActivity)
    }

    @Test
    fun `Given an activity is started, when currentActivity is accessed, then return the activity`() {
        val activity = mockk<Activity>()

        activityProvider.onActivityStarted(activity)

        assertEquals(activity, activityProvider.currentActivity)
    }

    @Test
    fun `Given an activity is resumed, when currentActivity is accessed, then return the activity`() {
        val activity = mockk<Activity>()

        activityProvider.onActivityResumed(activity)

        assertEquals(activity, activityProvider.currentActivity)
    }

    @Test
    fun `Given a new activity is created, when currentActivity is accessed, then return the latest activity`() {
        val activity1 = mockk<Activity>()
        val activity2 = mockk<Activity>()

        activityProvider.onActivityCreated(activity1, null)
        activityProvider.onActivityCreated(activity2, null)

        assertEquals(activity2, activityProvider.currentActivity)
    }

    @Test
    fun `Given the current activity is destroyed, when currentActivity is accessed, then return null`() {
        val activity = mockk<Activity>()

        activityProvider.onActivityCreated(activity, null)
        activityProvider.onActivityDestroyed(activity)

        assertNull(activityProvider.currentActivity)
    }

    @Test
    fun `Given an activity is destroyed that is not the current one, when currentActivity is accessed, then return the current activity`() {
        val activity1 = mockk<Activity>()
        val activity2 = mockk<Activity>()

        activityProvider.onActivityCreated(activity1, null)
        activityProvider.onActivityDestroyed(activity2)

        assertEquals(activity1, activityProvider.currentActivity)
    }
}
