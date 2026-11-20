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
import uk.gov.govuk.dwp.data.DwpLinkRepo
import uk.gov.govuk.dwp.di.DwpLinkModule.TEMPORARY_LINKING_TOKEN
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse

@OptIn(ExperimentalCoroutinesApi::class)
class DwpLinkViewModelTest {
    private val repository = mockk<DwpLinkRepo>()
    private val dwpAuthUrl = "http://dwp-auth.localhost"
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Uri::class)
        every {
            dwpAuthUrl.toUri().buildUpon().appendQueryParameter("token", "verification").build().toString()
        } returns "$dwpAuthUrl?token=verification"
        coEvery { repository.getVerification() } returns Result.Success(VerificationResponse("verification"))
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `Opening and dismissing the intro does not link`() = runTest(dispatcher) {
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Default, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.getVerification() }
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Continue waits for linking to finish and ignores repeated submissions`() = runTest(dispatcher) {
        val linking = CompletableDeferred<Unit>()
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } coAnswers {
            linking.await()
            Result.Success(Unit)
        }
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)

        viewModel.onIntroContinueClicked()
        viewModel.onIntroContinueClicked()
        runCurrent()
        assertEquals(DwpLinkViewModel.UiState.Loading.Linking, viewModel.uiState.value)
        viewModel.onIntroContinueClicked()
        linking.complete(Unit)
        advanceUntilIdle()

        assertEquals(DwpLinkViewModel.UiState.Success, viewModel.uiState.value)
        viewModel.onIntroContinueClicked()
        coVerify(exactly = 1) { repository.linkAccount(TEMPORARY_LINKING_TOKEN) }
    }

    @Test
    fun `A failed repository result can be retried`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Error()
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Error.Other, viewModel.uiState.value)

        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        viewModel.onRetryClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `Verification failure stops before sign-in and linking`() = runTest(dispatcher) {
        coEvery { repository.getVerification() } returns Result.Error()
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Error.Other, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Link API failure does not show success`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.ServiceNotResponding(500)
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Error.Other, viewModel.uiState.value)
    }

    @Test
    fun `Repeated Continue while verifying starts only one verification`() = runTest(dispatcher) {
        val verification = CompletableDeferred<Result<VerificationResponse>>()
        coEvery { repository.getVerification() } coAnswers { verification.await() }
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        viewModel.onIntroContinueClicked()
        runCurrent()
        assertEquals(DwpLinkViewModel.UiState.Loading.Auth, viewModel.uiState.value)
        verification.complete(Result.Success(VerificationResponse("verification")))
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Success, viewModel.uiState.value)
        coVerify(exactly = 1) { repository.getVerification() }
        coVerify(exactly = 1) { repository.linkAccount(TEMPORARY_LINKING_TOKEN) }
    }

    @Test
    fun `Empty verification token cannot proceed to sign-in`() = runTest(dispatcher) {
        coEvery { repository.getVerification() } returns Result.Success(VerificationResponse(""))
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Error.Other, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Verification completes before the simulated browser result is linked`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        coVerifyOrder {
            repository.getVerification()
            repository.linkAccount(TEMPORARY_LINKING_TOKEN)
        }
        assertEquals("$dwpAuthUrl?token=verification", viewModel.authUrlToLaunch.value)
        assertEquals(DwpLinkViewModel.UiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `Given the linking screen closes while verifying, then cancel without linking`() = runTest(dispatcher) {
        val pending = CompletableDeferred<Result<VerificationResponse>>()
        coEvery { repository.getVerification() } coAnswers { pending.await() }
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
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
        coEvery { repository.getVerification() } returns Result.DeviceOffline()
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Error.Offline, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.linkAccount(any()) }
    }

    @Test
    fun `Given linking is offline, when retried successfully, then show Success`() = runTest(dispatcher) {
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.DeviceOffline()
        val viewModel = DwpLinkViewModel(repository, dwpAuthUrl)
        viewModel.onIntroContinueClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Error.Offline, viewModel.uiState.value)
        coEvery { repository.linkAccount(TEMPORARY_LINKING_TOKEN) } returns Result.Success(Unit)
        viewModel.onRetryClicked()
        advanceUntilIdle()
        assertEquals(DwpLinkViewModel.UiState.Success, viewModel.uiState.value)
    }
}
