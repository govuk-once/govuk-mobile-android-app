package uk.gov.govuk.dvla.mapper

import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.VehicleColour
import uk.gov.govuk.dvla.domain.VehicleDetails
import uk.gov.govuk.dvla.util.StringProvider

internal fun VehicleDetails.getVehicleColour(stringProvider: StringProvider): String {
    val colourRes = stringProvider.getString(this.colour.getResource())

    return when (this.secondaryColour) {
        null, VehicleColour.NOT_STATED, VehicleColour.UNKNOWN -> colourRes
        else -> {
            val secondaryColourRes = stringProvider.getString(this.secondaryColour.getResource())
            stringProvider.getString(
                R.string.concatenated_vehicle_colours,
                colourRes,
                secondaryColourRes.lowercase()
            )
        }
    }
}

fun VehicleColour.getResource() = when (this) {
    VehicleColour.BROWN -> R.string.brown
    VehicleColour.BRONZE -> R.string.bronze
    VehicleColour.RED -> R.string.red
    VehicleColour.PINK -> R.string.pink
    VehicleColour.ORANGE -> R.string.orange
    VehicleColour.YELLOW -> R.string.yellow
    VehicleColour.GOLD -> R.string.gold
    VehicleColour.GREEN -> R.string.green
    VehicleColour.BLUE -> R.string.blue
    VehicleColour.PURPLE -> R.string.purple
    VehicleColour.GREY -> R.string.grey
    VehicleColour.SILVER -> R.string.silver
    VehicleColour.WHITE -> R.string.white
    VehicleColour.BLACK -> R.string.black
    VehicleColour.MULTI_COLOUR -> R.string.multi_colour
    VehicleColour.BEIGE -> R.string.beige
    VehicleColour.MAROON -> R.string.maroon
    VehicleColour.TURQUOISE -> R.string.turquoise
    VehicleColour.CREAM -> R.string.cream
    VehicleColour.NOT_STATED -> R.string.not_stated
    else -> R.string.not_stated
}
