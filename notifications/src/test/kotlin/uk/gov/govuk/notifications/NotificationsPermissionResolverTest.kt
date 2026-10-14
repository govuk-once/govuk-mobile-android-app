package uk.gov.govuk.notifications

import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.notifications.data.NotificationsRepo

@OptIn(ExperimentalPermissionsApi::class)
class NotificationsPermissionResolverTest {
    private val repo = mockk<NotificationsRepo>(relaxed = true)
    private lateinit var resolver: NotificationsPermissionResolver

    @Before
    fun setup() {
        resolver = NotificationsPermissionResolver(repo)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given sdk less than 33, when resolve is called, then returns SETTINGS`() {
        val status = mockk<PermissionStatus>(relaxed = true)

        runTest {
            val result = resolver.resolve(status, sdk = Build.VERSION_CODES.TIRAMISU - 1)

            assertEquals(NotificationsPermissionResolver.PermissionPath.SETTINGS, result)
        }
    }

    @Test
    fun `Given sdk 33 or higher and permission granted, when resolve is called, then returns SETTINGS`() {
        val status = mockk<PermissionStatus>()
        every { status.isGranted } returns true

        runTest {
            val result = resolver.resolve(status, sdk = Build.VERSION_CODES.TIRAMISU)

            assertEquals(NotificationsPermissionResolver.PermissionPath.SETTINGS, result)
        }
    }

    @Test
    fun `Given sdk 33 or higher, permission not granted, and first request not completed, when resolve is called, then returns OS_PROMPT`() {
        val status = mockk<PermissionStatus>()
        every { status.isGranted } returns false
        coEvery { repo.isFirstPermissionRequestCompleted() } returns false

        runTest {
            val result = resolver.resolve(status, sdk = Build.VERSION_CODES.TIRAMISU)

            assertEquals(NotificationsPermissionResolver.PermissionPath.OS_PROMPT, result)
        }
    }

    @Test
    fun `Given sdk 33 or higher, permission not granted, first request completed, and should show rationale is true, when resolve is called, then returns OS_PROMPT`() {
        val status = mockk<PermissionStatus>()
        every { status.isGranted } returns false
        every { status.shouldShowRationale } returns true
        coEvery { repo.isFirstPermissionRequestCompleted() } returns true

        runTest {
            val result = resolver.resolve(status, sdk = Build.VERSION_CODES.TIRAMISU)

            assertEquals(NotificationsPermissionResolver.PermissionPath.OS_PROMPT, result)
        }
    }

    @Test
    fun `Given sdk 33 or higher, permission not granted, first request completed, and should show rationale is false, when resolve is called, then returns SETTINGS`() {
        val status = mockk<PermissionStatus>()
        every { status.isGranted } returns false
        every { status.shouldShowRationale } returns false
        coEvery { repo.isFirstPermissionRequestCompleted() } returns true

        runTest {
            val result = resolver.resolve(status, sdk = Build.VERSION_CODES.TIRAMISU)

            assertEquals(NotificationsPermissionResolver.PermissionPath.SETTINGS, result)
        }
    }

    @Test
    fun `Given request OS permission is called, when permission request returns true, then returns true and calls required repo methods`() {
        coEvery { repo.requestPermission() } returns true

        runTest {
            val result = resolver.requestOsPermission()

            assertEquals(true, result)
            coVerify(exactly = 1) {
                repo.firstPermissionRequestCompleted()
                repo.giveConsent()
                repo.requestPermission()
            }
        }
    }

    @Test
    fun `Given request OS permission is called, when permission request returns false, then returns false and calls required repo methods`() {
        coEvery { repo.requestPermission() } returns false

        runTest {
            val result = resolver.requestOsPermission()

            assertEquals(false, result)
            coVerify(exactly = 1) {
                repo.firstPermissionRequestCompleted()
                repo.giveConsent()
                repo.requestPermission()
            }
        }
    }
}
