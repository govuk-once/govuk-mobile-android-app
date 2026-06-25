package uk.gov.govuk.analytics

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityProvider @Inject constructor() : ActivityProviderInterface, Application.ActivityLifecycleCallbacks {
    private var activityReference = WeakReference<Activity>(null)

    override val currentActivity: Activity?
        get() = activityReference.get()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityReference = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        activityReference = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        activityReference = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        // unimplemented as the referenced activity in this state is still valid
    }

    override fun onActivityStopped(activity: Activity) {
        // unimplemented as the referenced activity in this state is still valid
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // unimplemented as only interested in tracking the activity for UI display
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activityReference.get() == activity) {
            activityReference.clear()
        }
    }
}
