package uk.gov.govuk.dvla.data

import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

class DeviceIdProviderTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private lateinit var deviceIdProvider: DeviceIdProvider

    @Before
    fun setup() {
        deviceIdProvider = DeviceIdProvider(dataStore)
    }

    @After
    fun teardown() {
        unmockkStatic(Settings.Secure::class)
    }

    @Test
    fun `Given UUID exists, when getDeviceId is called, then return the id`() = runTest{
        val expectedId = "1468a863-6715-46b2-ac3e-d348555f5999"
        val prefKey = stringPreferencesKey("app_scoped_device_id")

        val preferences = preferencesOf(prefKey to expectedId)
        every { dataStore.data } returns flowOf(preferences)

        val result = deviceIdProvider.getDeviceId()

        assertEquals(expectedId, result)
        coVerify(exactly = 0) { dataStore.updateData(any()) }
    }

    @Test
    fun `Given id does not exist in DataStore, when getDeviceId is called, then generate a new UUID`() = runTest {
        every { dataStore.data } returns flowOf(emptyPreferences())
        coEvery { dataStore.updateData(any()) } returns emptyPreferences()

        val result = deviceIdProvider.getDeviceId()

        val uuid = UUID.fromString(result)
        assertEquals(result, uuid.toString())
        coVerify(exactly = 1) { dataStore.updateData(any()) }
    }
}