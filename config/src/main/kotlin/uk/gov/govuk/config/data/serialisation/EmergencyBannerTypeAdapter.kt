package uk.gov.govuk.config.data.serialisation

import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType

/**
 * Type adapter for EmergencyBannerType to safeguard against unknown types.
 * Defaults to INFORMATION if there's no match (for example misspelling)
 */
class EmergencyBannerTypeAdapter : TypeAdapter<EmergencyBannerType>() {

    private val bannerTypeMap: Map<String, EmergencyBannerType> =
        EmergencyBannerType.entries.associateBy { enumValue ->
            val name = enumValue::class.java.getField(enumValue.name)
                .getAnnotation(SerializedName::class.java)?.value

            require(!name.isNullOrBlank()) {
                "Missing @SerializedName annotation for enum constant ${enumValue.name}."
            }
            name
        }

    override fun read(reader: JsonReader): EmergencyBannerType {
        val typeString = reader.nextString()
        return bannerTypeMap[typeString] ?: EmergencyBannerType.INFORMATION // default to INFORMATION
    }

    override fun write(out: JsonWriter, value: EmergencyBannerType) {
        val errorMessage =
            "Internal Error: Missing serialized name for enum constant ${value.name}."
        val entry = bannerTypeMap.entries.find { it.value == value }
        requireNotNull(entry) { errorMessage }

        out.value(entry.key)
    }
}