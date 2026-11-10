package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.common.FuelType as RemoteFuelType

enum class FuelType {
    PETROL,
    DIESEL,
    ELECTRICITY,
    STEAM,
    GAS,
    PETROL_GAS,
    GAS_BI_FUEL,
    HYBRID_ELECTRIC,
    GAS_DIESEL,
    FUEL_CELLS,
    ELECTRIC_DIESEL,
    OTHER
}

internal fun RemoteFuelType?.toDomain(): FuelType =
    when (this) {
        RemoteFuelType.PETROL -> FuelType.PETROL
        RemoteFuelType.DIESEL -> FuelType.DIESEL
        RemoteFuelType.ELECTRICITY -> FuelType.ELECTRICITY
        RemoteFuelType.STEAM -> FuelType.STEAM
        RemoteFuelType.GAS -> FuelType.GAS
        RemoteFuelType.PETROL_GAS -> FuelType.PETROL_GAS
        RemoteFuelType.GAS_BI_FUEL -> FuelType.GAS_BI_FUEL
        RemoteFuelType.HYBRID_ELECTRIC -> FuelType.HYBRID_ELECTRIC
        RemoteFuelType.GAS_DIESEL -> FuelType.GAS_DIESEL
        RemoteFuelType.FUEL_CELLS -> FuelType.FUEL_CELLS
        RemoteFuelType.ELECTRIC_DIESEL -> FuelType.ELECTRIC_DIESEL
        RemoteFuelType.OTHER -> FuelType.OTHER
        else -> FuelType.OTHER
    }
