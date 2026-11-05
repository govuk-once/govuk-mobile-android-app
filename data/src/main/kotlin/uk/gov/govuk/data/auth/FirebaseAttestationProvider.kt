package uk.gov.govuk.data.auth

import com.google.firebase.appcheck.FirebaseAppCheck
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAttestationProvider @Inject constructor(
    private val appCheck: FirebaseAppCheck
): AttestationProvider {

    override suspend fun getToken(): String? = suspendCoroutine { continuation ->
        appCheck.getAppCheckToken(false)
            .addOnSuccessListener { result ->
                continuation.resume(result.token)
            }
            .addOnFailureListener { _ ->
                continuation.resume(null)
            }
    }

}