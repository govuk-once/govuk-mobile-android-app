package uk.gov.govuk.notifications

import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.notifications.data.NotificationsRepo

@OptIn(ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class)
class NotificationsPermissionViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val permissionStatus = mockk<PermissionStatus>()
    private val notificationsRepo = mockk<NotificationsRepo>()

    private lateinit var viewModel: NotificationsPermissionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsPermissionViewModel(
            notificationsRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is not granted, first permission request is not completed and should show rationale is false, When init, then ui state should be default`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted()} returns false
        every { permissionStatus.shouldShowRationale } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Default)
        }
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is not granted, first permission request is completed and should show rationale is true, When init, then ui state should be default`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted()} returns true
        every { permissionStatus.shouldShowRationale } returns true

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Default)
        }
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is not granted, first permission request is completed and should show rationale is false, When init, then ui state should be alert`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted()} returns true
        every { permissionStatus.shouldShowRationale } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Alert)
        }
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is granted, first permission request is not completed and should show rationale is false, When init, then ui state should be alert`() {
        every { permissionStatus.isGranted } returns true
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted()} returns false
        every { permissionStatus.shouldShowRationale } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Alert)
        }
    }

    @Test
    fun `Given the Android version is less than 12, When init, then ui state should be alert`() {
        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.S_V2)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Alert)
        }
    }
}
