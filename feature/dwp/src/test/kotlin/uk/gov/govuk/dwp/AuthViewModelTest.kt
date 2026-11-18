package uk.gov.govuk.dwp

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dwp.data.AuthRepo
import uk.gov.govuk.dwp.di.AuthModule.TEMPORARY_LINKING_TOKEN
import uk.gov.govuk.dwp.linking.data.LinkingRepo
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val repository = mockk<AuthRepo>()
    private val linkingRepo = mockk<LinkingRepo>()
    private val dwpAuthUrl = "https://dwp-auth/sign-in"
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Uri::class)
        every {
            dwpAuthUrl.toUri().buildUpon().appendQueryParameter("token", "verification").build().toString()
        } returns "$dwpAuthUrl?token=verification"
        coEvery { linkingRepo.getVerification() } returns Result.Success(VerificationResponse("verification"))
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `Opening and dismissing the intro does not link`() = runTest(dispatcher) {
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Default, viewModel.uiState.value)
        coVerify(exactly = 0) { linkingRepo.getVerification() }
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Continue waits for linking to finish and ignores repeated submissions`() = runTest(dispatcher) {
        val linking = CompletableDeferred<Unit>()
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } coAnswers {
            linking.await()
            Result.Success(Unit)
        }
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)

        viewModel.onIntroContinueClicked()
        viewModel.onIntroContinueClicked()
        runCurrent()
        assertEquals(AuthViewModel.UiState.Loading.Linking, viewModel.uiState.value)
        viewModel.onIntroContinueClicked()
        linking.complete(Unit)
        advanceUntilIdle()

        assertEquals(AuthViewModel.UiState.Success, viewModel.uiState.value)
        viewModel.onIntroContinueClicked()
        coVerify(exactly = 1) { repository.linkAccount(TEMPORARY_LINKING_TOKEN) }
    }

    @Test
    fun `A failed repository result can be retried`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Error()
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Error.Other, viewModel.uiState.value)

        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        viewModel.onRetryClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `Verification failure stops before sign-in and linking`() = runTest(dispatcher) {
        coEvery { linkingRepo.getVerification() } returns Result.Error()
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Error.Other, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Link API failure does not show success`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.ServiceNotResponding(500)
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Error.Other, viewModel.uiState.value)
    }

    @Test
    fun `Repeated Continue while verifying starts only one verification`() = runTest(dispatcher) {
        val verification = CompletableDeferred<Result<VerificationResponse>>()
        coEvery { linkingRepo.getVerification() } coAnswers { verification.await() }
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        viewModel.onIntroContinueClicked()
        runCurrent()
        assertEquals(AuthViewModel.UiState.Loading.Auth, viewModel.uiState.value)
        verification.complete(Result.Success(VerificationResponse("verification")))
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Success, viewModel.uiState.value)
        coVerify(exactly = 1) { linkingRepo.getVerification() }
        coVerify(exactly = 1) { repository.linkAccount(TEMPORARY_LINKING_TOKEN) }
    }

    @Test
    fun `Empty verification token cannot proceed to sign-in`() = runTest(dispatcher) {
        coEvery { linkingRepo.getVerification() } returns Result.Success(VerificationResponse(""))
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Error.Other, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Verification completes before the simulated browser result is linked`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        coVerifyOrder {
            linkingRepo.getVerification()
            repository.linkAccount(TEMPORARY_LINKING_TOKEN)
        }
        assertEquals(AuthViewModel.UiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `Given the linking screen closes while verifying, then cancel without linking`() = runTest(dispatcher) {
        val pending = CompletableDeferred<Result<VerificationResponse>>()
        coEvery { linkingRepo.getVerification() } coAnswers { pending.await() }
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        val store = ViewModelStore()
        store.put("dwp", viewModel)
        viewModel.onIntroContinueClicked()
        runCurrent()
        store.clear()
        pending.complete(Result.Success(VerificationResponse("verification")))
        advanceUntilIdle()
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Given verification is offline, then show Offline error without linking`() = runTest(dispatcher) {
        coEvery { linkingRepo.getVerification() } returns Result.DeviceOffline()
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Error.Offline, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Given linking is offline, when retried successfully, then show Success`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.DeviceOffline()
        val viewModel = AuthViewModel(repository, linkingRepo, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Error.Offline, viewModel.uiState.value)
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        viewModel.onRetryClicked()
        advanceUntilIdle()
        assertEquals(AuthViewModel.UiState.Success, viewModel.uiState.value)
    }
}
