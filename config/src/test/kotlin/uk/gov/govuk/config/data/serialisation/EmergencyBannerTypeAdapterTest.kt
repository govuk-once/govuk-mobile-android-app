package uk.gov.govuk.config.data.serialisation

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType
import java.io.StringReader
import java.io.StringWriter

class EmergencyBannerTypeAdapterTest {

    private lateinit var gson: Gson

    @Before
    fun setup() {
        gson = GsonBuilder()
            .registerTypeAdapter(EmergencyBannerType::class.java, EmergencyBannerTypeAdapter())
            .create()
    }

    @Test
    fun `read should return INFORMATION for an unknown or null type string`() {
        val adapter = EmergencyBannerTypeAdapter()

        val unknownValue = JsonReader(StringReader("\"not-found-key\""))
        assertEquals(EmergencyBannerType.INFORMATION, adapter.read(unknownValue))

        val emptyValue = JsonReader(StringReader("\"\""))
        assertEquals(EmergencyBannerType.INFORMATION, adapter.read(emptyValue))
    }

    @Test
    fun `read should return NOTABLE_DEATH for the known serialized name`() {
        val adapter = EmergencyBannerTypeAdapter()
        val reader = JsonReader(StringReader("\"notable-death\""))
        assertEquals(EmergencyBannerType.NOTABLE_DEATH, adapter.read(reader))
    }

    @Test
    fun `EmergencyBannerType should correctly write all banner types`() {
        val adapter = EmergencyBannerTypeAdapter()

        EmergencyBannerType.entries.forEach { type ->
            val writer = StringWriter()
            val jsonWriter = JsonWriter(writer)

            adapter.write(jsonWriter, type)

            val serializedName = type.name.lowercase().replace("_", "-")
            val expectedOutput = "\"$serializedName\""

            assertEquals(expectedOutput, writer.toString())
        }
    }
}