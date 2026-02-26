package uk.gov.govuk.notifications

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationsPermissionProviderTest {
    private val activity = mockk<Activity>()

    @Before
    fun setup() {
        mockkStatic(ActivityCompat::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given should show request permission rationale is false and Android version is 33, When get notifications permission should show rationale called, Then should return false`() {
        every {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns false

        runTest {
            val shouldShowRationale =
                notificationsPermissionShouldShowRationale(activity, Build.VERSION_CODES.TIRAMISU)
            assertFalse(shouldShowRationale)
        }
    }

    @Test
    fun `Given should show request permission rationale is true and Android version is 33, When get notifications permission should show rationale called, Then should return true`() {
        every {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns true

        runTest {
            val shouldShowRationale =
                notificationsPermissionShouldShowRationale(activity, Build.VERSION_CODES.TIRAMISU)
            assertTrue(shouldShowRationale)
        }
    }

    @Test
    fun `Given should show request permission rationale is false and Android version is 32, When get notifications permission should show rationale called, Then should return false`() {
        every {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns false

        runTest {
            val shouldShowRationale =
                notificationsPermissionShouldShowRationale(activity, Build.VERSION_CODES.S_V2)
            assertFalse(shouldShowRationale)
        }
    }

    @Test
    fun `Given should show request permission rationale is true and Android version is 32, When get notifications permission should show rationale called, Then should return false`() {
        every {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns true

        runTest {
            val shouldShowRationale =
                notificationsPermissionShouldShowRationale(activity, Build.VERSION_CODES.S_V2)
            assertFalse(shouldShowRationale)
        }
    }
}
