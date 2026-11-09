package uk.gov.govuk.config.data.remote.source

import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseConfigDataSourceTest {

    private val firebaseRemoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)
    private val dataSource = FirebaseConfigDataSource(firebaseRemoteConfig)

    private fun <T> mockSuccessTask(resultValue: T? = null): Task<T> {
        val task = mockk<Task<T>>()
        every { task.isComplete } returns true
        every { task.isCanceled } returns false
        every { task.exception } returns null
        every { task.result } returns resultValue
        return task
    }

    private fun <T> mockFailureTask(exception: Exception): Task<T> {
        val task = mockk<Task<T>>()
        every { task.isComplete } returns true
        every { task.isCanceled } returns false
        every { task.exception } returns exception
        return task
    }

    @Test
    fun `Given reset succeeds, when clearRemoteValues is called, then reset and re-apply settings`() =
        runTest {
            every { firebaseRemoteConfig.reset() } returns mockSuccessTask(null)

            dataSource.clearRemoteValues()

            verify(ordering = Ordering.ORDERED) {
                firebaseRemoteConfig.reset()
                firebaseRemoteConfig.setConfigSettingsAsync(any())
                firebaseRemoteConfig.setDefaultsAsync(any<Int>())
            }
        }

    @Test
    fun `Given reset fails, when clearRemoteValues is called, settings are still reapplied`() = runTest {
        every { firebaseRemoteConfig.reset() } throws Exception("Exception")

        dataSource.clearRemoteValues()

        verify {
            firebaseRemoteConfig.setConfigSettingsAsync(any())
            firebaseRemoteConfig.setDefaultsAsync(any<Int>())
        }
    }

    @Test
    fun `Given fetch succeeds, when fetch is called, then return true`() = runTest {
        every { firebaseRemoteConfig.fetch() } returns mockSuccessTask(null)

        val result = dataSource.fetch()
        assertTrue(result)
    }

    @Test
    fun `Given fetch task fails, when fetch is called, then return false`() = runTest {
        every { firebaseRemoteConfig.fetch() } returns mockFailureTask(Exception("Exception"))

        val result = dataSource.fetch()
        assertFalse(result)
    }

    @Test
    fun `Given fetch throws immediate exception, when fetch is called, then return false`() =
        runTest {
            every { firebaseRemoteConfig.fetch() } throws Exception("Exception")

            val result = dataSource.fetch()
            assertFalse(result)
        }

    @Test
    fun `Given activate succeeds, when activate is called, then return true`() = runTest {
        every { firebaseRemoteConfig.activate() } returns mockSuccessTask(true)

        val result = dataSource.activate()
        assertTrue(result)
    }

    @Test
    fun `Given activate task fails, when activate is called, then return false`() = runTest {
        every { firebaseRemoteConfig.activate() } returns mockFailureTask(Exception("Exception"))

        val result = dataSource.activate()
        assertFalse(result)
    }

    @Test
    fun `Given activate throws exception, when activate is called, then return false`() = runTest {
        every { firebaseRemoteConfig.activate() } throws Exception("Exception")

        val result = dataSource.activate()
        assertFalse(result)
    }

    @Test
    fun `Given fetch succeeds and activate succeeds, when fetchAndActivate is called, then return true`() = runTest {
        every { firebaseRemoteConfig.fetch() } returns mockSuccessTask(null)
        every { firebaseRemoteConfig.activate() } returns mockSuccessTask(true)

        val result = dataSource.fetchAndActivate()

        assertTrue(result)
        verify { firebaseRemoteConfig.activate() }
    }

    @Test
    fun `Given fetch fails, when fetchAndActivate is called, then return false and do not call activate`() = runTest {
        every { firebaseRemoteConfig.fetch() } returns mockFailureTask(Exception("Exception"))

        val result = dataSource.fetchAndActivate()

        assertFalse(result)
        verify(exactly = 0) { firebaseRemoteConfig.activate() }
    }

    @Test
    fun `Given fetch succeeds but activate fails, when fetchAndActivate is called, then return false`() = runTest {
        every { firebaseRemoteConfig.fetch() } returns mockSuccessTask(null)
        every { firebaseRemoteConfig.activate() } returns mockFailureTask(Exception("Activate Failed"))

        val result = dataSource.fetchAndActivate()

        assertFalse(result)
    }
}