package uk.gov.govuk.dvla.remote.model.common

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
    OTHER
}
