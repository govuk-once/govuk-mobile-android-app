package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.R

enum class VehicleColour {
    @SerializedName("BROWN")
    BROWN,

    @SerializedName("BRONZE")
    BRONZE,

    @SerializedName("RED")
    RED,

    @SerializedName("PINK")
    PINK,

    @SerializedName("ORANGE")
    ORANGE,

    @SerializedName("YELLOW")
    YELLOW,

    @SerializedName("GOLD")
    GOLD,

    @SerializedName("GREEN")
    GREEN,

    @SerializedName("BLUE")
    BLUE,

    @SerializedName("PURPLE")
    PURPLE,

    @SerializedName("GREY")
    GREY,

    @SerializedName("SILVER")
    SILVER,

    @SerializedName("WHITE")
    WHITE,

    @SerializedName("BLACK")
    BLACK,

    @SerializedName("MULTI-COLOUR")
    MULTI_COLOUR,

    @SerializedName("BEIGE")
    BEIGE,

    @SerializedName("MAROON")
    MAROON,

    @SerializedName("TURQUOISE")
    TURQUOISE,

    @SerializedName("CREAM")
    CREAM,

    @SerializedName("NOT STATED")
    NOT_STATED;

    fun getResource() = when (this) {
        BROWN -> R.string.brown
        BRONZE -> R.string.bronze
        RED -> R.string.red
        PINK -> R.string.pink
        ORANGE -> R.string.orange
        YELLOW -> R.string.yellow
        GOLD -> R.string.gold
        GREEN -> R.string.green
        BLUE -> R.string.blue
        PURPLE -> R.string.purple
        GREY -> R.string.grey
        SILVER -> R.string.silver
        WHITE -> R.string.white
        BLACK -> R.string.black
        MULTI_COLOUR -> R.string.multi_colour
        BEIGE -> R.string.beige
        MAROON -> R.string.maroon
        TURQUOISE -> R.string.turquoise
        CREAM -> R.string.cream
        NOT_STATED -> R.string.not_stated
    }
}
