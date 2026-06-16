package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.common.VehicleColour as RemoteVehicleColour

enum class VehicleColour {
    BROWN,
    BRONZE,
    RED,
    PINK,
    ORANGE,
    YELLOW,
    GOLD,
    GREEN,
    BLUE,
    PURPLE,
    GREY,
    SILVER,
    WHITE,
    BLACK,
    MULTI_COLOUR,
    BEIGE,
    MAROON,
    TURQUOISE,
    CREAM,
    NOT_STATED
}

internal fun RemoteVehicleColour?.toDomain() =
    when (this) {
        RemoteVehicleColour.BROWN -> VehicleColour.BROWN
        RemoteVehicleColour.BRONZE -> VehicleColour.BRONZE
        RemoteVehicleColour.RED -> VehicleColour.RED
        RemoteVehicleColour.PINK -> VehicleColour.PINK
        RemoteVehicleColour.ORANGE -> VehicleColour.ORANGE
        RemoteVehicleColour.YELLOW -> VehicleColour.YELLOW
        RemoteVehicleColour.GOLD -> VehicleColour.GOLD
        RemoteVehicleColour.GREEN -> VehicleColour.GREEN
        RemoteVehicleColour.BLUE -> VehicleColour.BLUE
        RemoteVehicleColour.PURPLE -> VehicleColour.PURPLE
        RemoteVehicleColour.GREY -> VehicleColour.GREY
        RemoteVehicleColour.SILVER -> VehicleColour.SILVER
        RemoteVehicleColour.WHITE -> VehicleColour.WHITE
        RemoteVehicleColour.BLACK -> VehicleColour.BLACK
        RemoteVehicleColour.MULTI_COLOUR -> VehicleColour.MULTI_COLOUR
        RemoteVehicleColour.BEIGE -> VehicleColour.BEIGE
        RemoteVehicleColour.MAROON -> VehicleColour.MAROON
        RemoteVehicleColour.TURQUOISE -> VehicleColour.TURQUOISE
        RemoteVehicleColour.CREAM -> VehicleColour.CREAM
        RemoteVehicleColour.NOT_STATED -> VehicleColour.NOT_STATED
        else -> null
    }
