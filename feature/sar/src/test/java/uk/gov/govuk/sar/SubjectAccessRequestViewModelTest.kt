package uk.gov.govuk.sar

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.sar.data.SubjectAccessRequestFile
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectAccessRequestViewModelTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val context: Context = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SubjectAccessRequestViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { context.filesDir } returns temporaryFolder.root

        viewModel = SubjectAccessRequestViewModel(analyticsClient, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveUserData creates a User profile file`() = runTest {
        viewModel.saveUserData(testDispatcher)
        advanceUntilIdle()

        val file = File(temporaryFolder.root, SubjectAccessRequestFile.FILENAME)

        assertTrue(file.exists())
        assertTrue(file.readText().contains("1234"))
    }

    @Test
    fun `loadUserData returns the correct profile when the file exists`() = runTest {
        val file = File(temporaryFolder.root, SubjectAccessRequestFile.FILENAME)
        file.writeText("""{"notifications":{"consentStatus":"ACCEPTED","pushId":"999"}}""")

        viewModel.loadUserData()
        advanceUntilIdle()

        assertEquals(ConsentStatus.ACCEPTED, viewModel.userProfile.value?.notifications?.consentStatus)
        assertEquals("999", viewModel.userProfile.value?.notifications?.pushId)
    }

    @Test
    fun `loadUserData returns a dummy user profile when the file does not exit`() = runTest {
        viewModel.loadUserData()
        advanceUntilIdle()

        assertEquals(ConsentStatus.UNKNOWN, viewModel.userProfile.value?.notifications?.consentStatus)
        assertEquals("0000", viewModel.userProfile.value?.notifications?.pushId)
    }

    @Test
    fun `Given an explainer page view, then log analytics`() {
        viewModel.onExplainerPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SubjectAccessRequestExplainerScreen",
                screenName = "Subject Access Request Explainer",
                title = "Subject Access Request Explainer"
            )
        }
    }

    @Test
    fun `Given an display page view, then log analytics`() {
        viewModel.onDisplayPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SubjectAccessRequestDisplayScreen",
                screenName = "Subject Access Request Display",
                title = "Subject Access Request Display"
            )
        }
    }

    @Test
    fun `Given a button click, then log analytics`() {
        viewModel.onButtonClick("Text")

        verify {
            analyticsClient.buttonClick(
                text = "Text",
                section = "Subject Access Request"
            )
        }
    }
}
