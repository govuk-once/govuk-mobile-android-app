package uk.gov.govuk.notifications

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsPromptWidgetViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val notificationsProvider = mockk<NotificationsProvider>()
    private val notificationsDataStore = mockk<NotificationsDataStore>()

    private lateinit var viewModel: NotificationsPromptWidgetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsPromptWidgetViewModel(notificationsProvider, notificationsDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given on click, then call request permission`() {
        every { notificationsProvider.requestPermission() } returns Unit
        coEvery { notificationsDataStore.firstPermissionRequestCompleted() } returns Unit

        runTest {
            viewModel.onClick()

            coVerify(exactly = 1) {
                notificationsDataStore.firstPermissionRequestCompleted()
            }
            verify(exactly = 1) {
                notificationsProvider.requestPermission()
            }
        }
    }
}
