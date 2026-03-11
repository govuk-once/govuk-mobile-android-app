package uk.gov.govuk.dvla.data

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeviceIdProviderTest {

    private val context = mockk<Context>()
    private val contentResolver = mockk<ContentResolver>()
    private lateinit var deviceIdProvider: DeviceIdProvider

    @Before
    fun setup() {
        every { context.contentResolver } returns contentResolver

        mockkStatic(Settings.Secure::class)

        deviceIdProvider = DeviceIdProvider(context)
    }

    @After
    fun teardown() {
        unmockkStatic(Settings.Secure::class)
    }

    @Test
    fun `Given Android id exists, when getDeviceId is called, then return the id`() {
        val expectedId = "device_id"
        every {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } returns expectedId

        val result = deviceIdProvider.getDeviceId()

        assertEquals(expectedId, result)
    }

    @Test
    fun `Given Android id is null, When getDeviceId is called, then return a fallback id`() {
        every {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } returns null

        val result = deviceIdProvider.getDeviceId()

        assertTrue(result.startsWith("fallback_id_"))
    }
}