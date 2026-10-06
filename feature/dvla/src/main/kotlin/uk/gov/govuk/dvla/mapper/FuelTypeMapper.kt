package uk.gov.govuk.dvla.mapper

import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.FuelType

internal fun FuelType.getResources() = when (this) {
    FuelType.PETROL -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.petrol_summary,
        R.string.petrol_specification
    )

    FuelType.DIESEL -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.diesel_summary,
        R.string.diesel_specification
    )

    FuelType.ELECTRICITY -> Triple(
        R.drawable.ic_electric,
        R.string.electric_summary,
        R.string.electric_specification
    )

    FuelType.STEAM -> Triple(
        R.drawable.ic_steam,
        R.string.steam_summary,
        R.string.steam_specification
    )

    FuelType.GAS -> Triple(
        R.drawable.ic_gas,
        R.string.gas_summary,
        R.string.gas_specification
    )

    FuelType.PETROL_GAS -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.petrol_gas_summary,
        R.string.petrol_gas_specification
    )

    FuelType.GAS_BI_FUEL -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.gas_bi_fuel_summary,
        R.string.gas_bi_fuel_specification
    )

    FuelType.HYBRID_ELECTRIC -> Triple(
        R.drawable.ic_hybrid,
        R.string.hybrid_electric_summary,
        R.string.hybrid_electric_specification
    )

    FuelType.GAS_DIESEL -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.gas_diesel_summary,
        R.string.gas_diesel_specification
    )

    FuelType.FUEL_CELLS -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.fuel_cells_summary,
        R.string.fuel_cells_specification
    )

    FuelType.ELECTRIC_DIESEL -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.electric_diesel_summary,
        R.string.electric_diesel_specification
    )

    FuelType.OTHER -> Triple(
        R.drawable.ic_petrol_diesel,
        R.string.other,
        R.string.other
    )
}
