package uk.gov.govuk.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

const val FIREBASE_USER_PSEUDO_ID = "fb_user_pseudo_id" // appInstanceId
const val FIREBASE_SESSION_ID = "fb_session_id" //sessionId

/**
 * Firebase resolves the appInstanceId and sessionId asynchronously. Plus the
 * sessionId rotates over the life of the app. This caches the last known values
 * and refreshes them in background after each use - this way it should ensure
 * that the caller always has an available, best-effort value.
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
