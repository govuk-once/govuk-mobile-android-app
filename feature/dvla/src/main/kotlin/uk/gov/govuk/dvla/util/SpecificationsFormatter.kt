package uk.gov.govuk.dvla.util

fun getFormattedEngineCapacity(engineCapacity: Int): String {
    if (engineCapacity < 1000) return "${engineCapacity}cc"
    val capacityInLitres = "${engineCapacity / 100 / 10.0}L"
    return capacityInLitres
}

fun getFormattedVehicleColour(
    colour: String,
    concatenator: String,
    secondaryColour: String
): String {
    return "$colour $concatenator $secondaryColour"
}

fun getFormattedEngineCapacityAltText(engineCapacity: String, replacementText: String) =
    engineCapacity.replace("L", " $replacementText")

fun getFormattedEmissionsAltText(euroStatus: String, replacementText: String) =
    euroStatus.replace("g/km", " $replacementText")
