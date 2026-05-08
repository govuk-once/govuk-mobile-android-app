package uk.gov.govuk.sar.data

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectAccessRequestFileTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val context: Context = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var subject: SubjectAccessRequestFile

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { context.filesDir } returns temporaryFolder.root

        subject = SubjectAccessRequestFile(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `creates a subject access request file`() = runTest(testDispatcher) {
        val file = File(temporaryFolder.root, SubjectAccessRequestFile.FILENAME)

        assertFalse(file.exists())

        val user = User(
            Notifications(
                consentStatus = ConsentStatus.ACCEPTED,
                pushId = "999"
            )
        )

        subject.writeUserData(user)
        advanceUntilIdle()

        assertTrue(file.exists())
    }

    @Test
    fun `returns the correct user profile when subject access request file exists`() = runTest(testDispatcher) {
        val user = User(
            Notifications(
                consentStatus = ConsentStatus.DENIED,
                pushId = "666"
            )
        )

        subject.writeUserData(user)
        advanceUntilIdle()

        assertEquals(ConsentStatus.DENIED, subject.readUserData().notifications.consentStatus)
        assertEquals("666", subject.readUserData().notifications.pushId)
    }

    @Test
    fun `returns a dummy user profile when file is missing`() = runTest(testDispatcher) {
        val result = subject.readUserData()
        advanceUntilIdle()

        assertEquals(ConsentStatus.UNKNOWN, result.notifications.consentStatus)
        assertEquals("0000", result.notifications.pushId)
    }
}

