package uk.gov.govuk.login

import android.os.Build
import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.R
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricViewModelTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: BiometricViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = BiometricViewModel(authRepo, appRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given skip, then update repo`() {
        viewModel.onSkip()

        coVerify {
            appRepo.skipBiometrics()
        }
    }

    @Test
    fun `Given continue, when persist token success, then emit ui state`() {
        coEvery { authRepo.persistRefreshToken(any(), any()) } returns true

        viewModel.onContinue(activity)

        assertTrue(viewModel.uiState.value)
    }

    @Test
    fun `Given continue, when persist token failure, then emit ui state`() {
        coEvery { authRepo.persistRefreshToken(any(), any()) } returns false

        viewModel.onContinue(activity)

        assertFalse(viewModel.uiState.value)
    }

    @Test
    fun `Given the app is running on an Android 10 device, when get description one is called, then return the correct resource`() {
        val descriptionOne = viewModel.getDescriptionOne(Build.VERSION_CODES.Q)

        assertEquals(R.string.login_biometrics_android_10_description_1, descriptionOne)
    }

    @Test
    fun `Given the app is running on an Android 11 device, when get description one is called, then return the correct resource`() {
        val descriptionOne = viewModel.getDescriptionOne(Build.VERSION_CODES.R)

        assertEquals(R.string.login_biometrics_android_11_description_1, descriptionOne)
    }
}
