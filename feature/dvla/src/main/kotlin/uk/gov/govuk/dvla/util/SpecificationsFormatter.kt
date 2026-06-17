package uk.gov.govuk.dvla.util

internal fun getFormattedEngineCapacity(engineCapacity: Int): String {
    if (engineCapacity < 1000) return "${engineCapacity}cc"
    val capacityInLitres = "${engineCapacity / 100 / 10.0}L"
    return capacityInLitres
}

internal fun getFormattedVehicleColour(
    colour: String,
    concatenator: String,
    secondaryColour: String
) = "$colour $concatenator $secondaryColour"

internal fun getFormattedEngineCapacityAltText(engineCapacity: String, replacementText: String) =
    engineCapacity.replace("L", " $replacementText")

