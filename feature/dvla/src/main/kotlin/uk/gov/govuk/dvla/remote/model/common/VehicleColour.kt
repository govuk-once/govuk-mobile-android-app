package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

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
    NOT_STATED
}
