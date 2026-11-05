package uk.gov.govuk.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.data.local.AppDataStore

class AppRepoTest {

    private val appDataStore = mockk<AppDataStore>(relaxed = true)

    @Test
    fun `Given the user has not skipped biometrics, When has skipped biometrics, then return false`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.hasSkippedBiometrics() } returns false

        runTest {

            assertFalse(repo.hasSkippedBiometrics())
        }
    }

    @Test
    fun `Given the user has skipped biometrics, When has skipped biometrics, then return true`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.hasSkippedBiometrics() } returns true

        runTest {
            assertTrue(repo.hasSkippedBiometrics())
        }
    }

    @Test
    fun `Given the user skips biometrics, When skip biometrics, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.skipBiometrics()

            coVerify { appDataStore.skipBiometrics() }
        }
    }

    @Test
    fun `Given skip biometrics is cleared, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.clearBiometricsSkipped()

            coVerify { appDataStore.clearBiometricsSkipped() }
        }
    }

    @Test
    fun `Given the user has not previously completed topic selection, When is topic selection completed, then return false`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isTopicSelectionCompleted() } returns false

        runTest {

            assertFalse(repo.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed topic selection, When is topic selection completed, then return true`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isTopicSelectionCompleted() } returns true

        runTest {
            assertTrue(repo.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the user has completed topic selection, When topic selection completed, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.topicSelectionCompleted()

            coVerify { appDataStore.topicSelectionCompleted() }
        }
    }

    @Test
    fun `Given the data store contains suppressed home widgets, When the suppressed home widgets flow is requested, then forward the flow from the data store`() {
        val suppressedWidgets = flowOf(setOf("Widget 1"))
        every { appDataStore.suppressedHomeWidgets } returns suppressedWidgets

        val repo = AppRepo(appDataStore)

        runTest {
            assertEquals(suppressedWidgets, repo.suppressedHomeWidgets)
        }
    }

    @Test
    fun `Given the user has suppressed a home widget, When suppress home widget, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.suppressHomeWidget("id")

            coVerify { appDataStore.suppressHomeWidget("id") }
        }
    }

    @Test
    fun `Given a different user has logged in, When clear, then clear data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.clear()

            coVerify { appDataStore.clear() }
        }
    }
}