package uk.gov.govuk.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase resolves the appInstanceId and sessionId asynchronously. Plus the
 * sessionId rotates over the life of the app. This caches the last known values
 * and refreshes them in background after each use - this should ensure the caller(s)
 * always have an available, best-effort value.
 */
@Singleton
class FirebaseIdentifiers @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    @Volatile
    var userPseudoId: String? = null
        private set

    @Volatile
    var sessionId: String? = null
        private set

    fun refresh() {
        firebaseAnalytics.appInstanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userPseudoId = task.result
            }
        }

        firebaseAnalytics.sessionId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { sessionId = it.toString() }
            }
        }
    }
}
