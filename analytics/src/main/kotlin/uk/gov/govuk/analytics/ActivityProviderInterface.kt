package uk.gov.govuk.analytics

import android.app.Activity

interface ActivityProviderInterface {
    val currentActivity: Activity?

    fun addOnActivityDestroyedListener(listener: (activity: Activity) -> Unit)
}
