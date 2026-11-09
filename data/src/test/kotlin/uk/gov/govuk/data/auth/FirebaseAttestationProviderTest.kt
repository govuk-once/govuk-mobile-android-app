package uk.gov.govuk.data.auth

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class FirebaseAttestationProviderTest {

    private val appCheck = mockk<FirebaseAppCheck>(relaxed = true)
    private val task = mockk<Task<AppCheckToken>>(relaxed = true)
    private val appCheckToken = mockk<AppCheckToken>(relaxed = true)

    @Test
    fun `Given app check failure return null`() = runTest {
        every { appCheck.getAppCheckToken(any()) } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } answers {
            firstArg<OnFailureListener>().onFailure(IOException())
            task
        }

        val provider = FirebaseAttestationProvider(appCheck)
        assertNull(provider.getToken())
    }

    @Test
    fun `Given app check success return token`() = runTest {
        every { appCheck.getAppCheckToken(any()) } returns task
        every { task.addOnSuccessListener(any()) } answers {
            firstArg<OnSuccessListener<AppCheckToken>>().onSuccess(appCheckToken)
            task
        }
        every { task.addOnFailureListener(any()) } returns task
        every { appCheckToken.token } returns "token"

        val provider = FirebaseAttestationProvider(appCheck)
        assertEquals("token", provider.getToken())
    }
}