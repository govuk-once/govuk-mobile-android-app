package uk.gov.govuk.dvla.util

internal fun getFormattedEngineCapacity(engineCapacity: Int): String {
    if (engineCapacity < 1000) return "${engineCapacity}cc" // Append 'cc' for cubic centimeters
    val capacityRoundedToOneDecimalPlace = "%.1f".format(engineCapacity / 1000f)
    return "${capacityRoundedToOneDecimalPlace}L" // Append 'L' for Litres
}

internal fun getFormattedEngineCapacityAltText(engineCapacity: String, replacementText: String) =
    engineCapacity.replace("L", " $replacementText")

internal fun getFormattedYearAltText(year: String?) =
    year?.chunked(2)?.joinToString(separator = " ") // add a space between a string
