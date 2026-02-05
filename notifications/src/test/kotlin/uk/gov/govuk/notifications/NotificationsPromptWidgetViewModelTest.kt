package uk.gov.govuk.notifications

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.notifications.data.NotificationsRepo

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsPromptWidgetViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val notificationsRepo = mockk<NotificationsRepo>()

    private lateinit var viewModel: NotificationsPromptWidgetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsPromptWidgetViewModel(notificationsRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given on click, then call request permission`() {
        coEvery { notificationsRepo.firstPermissionRequestCompleted() } returns Unit
        coEvery { notificationsRepo.requestPermission() } returns Unit

        runTest {
            viewModel.onClick()

            coVerify(exactly = 1) {
                notificationsRepo.firstPermissionRequestCompleted()
                notificationsRepo.requestPermission()
            }
        }
    }
}
