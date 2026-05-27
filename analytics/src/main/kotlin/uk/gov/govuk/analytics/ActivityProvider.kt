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

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (activityReference.get() == activity) {
            activityReference.clear()
        }
    }
}
