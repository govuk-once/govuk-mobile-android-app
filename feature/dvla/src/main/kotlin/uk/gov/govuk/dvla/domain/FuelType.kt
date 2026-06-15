package uk.gov.govuk.dvla.domain

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.R

enum class FuelType {
    @SerializedName("PETROL")
    PETROL,

    @SerializedName("DIESEL")
    DIESEL,

    @SerializedName("ELECTRICITY")
    ELECTRICITY,

    @SerializedName("STEAM")
    STEAM,

    @SerializedName("GAS")
    GAS,

    @SerializedName("PETROL/GAS")
    PETROL_GAS,

    @SerializedName("GAS BI-FUEL")
    GAS_BI_FUEL,

    @SerializedName("HYBRID ELECTRIC")
    HYBRID_ELECTRIC,

    @SerializedName("GAS DIESEL")
    GAS_DIESEL,

    @SerializedName("FUEL CELLS")
    FUEL_CELLS,

    @SerializedName("ELECTRIC DIESEL")
    ELECTRIC_DIESEL,

    @SerializedName("OTHER")
    OTHER;

    fun getResources() = when (this) {
        PETROL -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.petrol)
        DIESEL -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.diesel)
        ELECTRICITY -> FuelTypeResources(R.drawable.ic_electric, R.string.electric)
        STEAM -> FuelTypeResources(R.drawable.ic_steam, R.string.steam)
        GAS -> FuelTypeResources(R.drawable.ic_gas, R.string.gas)
        PETROL_GAS -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.petrol_and_gas)
        GAS_BI_FUEL -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.gas_bi_fuel)
        HYBRID_ELECTRIC -> FuelTypeResources(R.drawable.ic_hybrid, R.string.hybrid_electric)
        GAS_DIESEL -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.gas_diesel)
        FUEL_CELLS -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.fuel_cells)
        ELECTRIC_DIESEL -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.electric_diesel)
        OTHER -> FuelTypeResources(R.drawable.ic_petrol_diesel, R.string.other)
    }
}

data class FuelTypeResources(val icon: Int, val label: Int)
