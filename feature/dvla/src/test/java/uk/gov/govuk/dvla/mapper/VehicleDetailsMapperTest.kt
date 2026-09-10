package uk.gov.govuk.dvla.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.FuelType
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleColour
import uk.gov.govuk.dvla.domain.VehicleDetails
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.util.StringProvider
import java.time.LocalDate

class VehicleDetailsMapperTest {

    private val stringProvider = mockk<StringProvider>()
    private val taxAndMotStatusMapper = TaxAndMotStatusMapper(stringProvider)
    private val mapper = VehicleDetailsMapper(stringProvider, taxAndMotStatusMapper)

    private companion object{
        const val FUEL_TYPE = "Fuel type"
        const val COLOUR = "Colour"
    }

    @Before
    fun setup() {
        every { stringProvider.getString(any<Int>(), *anyVararg()) } returns ""
        every { stringProvider.getString(R.string.fuel_type_title) } returns FUEL_TYPE
        every { stringProvider.getString(R.string.colour_title) } returns COLOUR
    }

    private fun makeVehicleDetails(
        keeperTitle: String? = "MR",
        keeperFirstNames: String? = "DAWN",
        keeperLastName: String? = "WILLIAMS",
        keeperFullAddress: String? = "Long View Rd\nMorriston\nSwansea\nSA6 7JL",
        colour: VehicleColour = VehicleColour.RED,
        secondaryColour: VehicleColour? = null,
        engineCapacity: Int? = 2000,
        dateOfFirstRegistration: LocalDate? = LocalDate.of(2020, 6, 1),
        exhaustEmissionsCo2: Int? = 199,
        model: String? = null,
        fuelType: FuelType = FuelType.PETROL
    ) = VehicleDetails(
        summary = VehicleSummary(
            vehicleId = 156487251,
            registration = "AA19 AAA",
            make = "FORD",
            model = model,
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = null,
            motStatus = MotStatus.VALID,
            motExpiryDate = null,
            sornStart = null,
            currentLicencePaymentMethod = null
        ),
        dateOfFirstRegistration = dateOfFirstRegistration,
        fuelType = fuelType,
        colour = colour,
        secondaryColour = secondaryColour,
        engineCapacity = engineCapacity,
        exhaustEmissionsCo2 = exhaustEmissionsCo2,
        keeperTitle = keeperTitle,
        keeperFirstNames = keeperFirstNames,
        keeperLastName = keeperLastName,
        keeperFullAddress = keeperFullAddress
    )

    @Test
    fun `Given a vehicle with full keeper details, when mapped, the keeper name and address are assembled correctly`() {
        val result = mapper.toUiModel(makeVehicleDetails(), dvlaUrls = null)

        assertEquals("MR DAWN WILLIAMS", result.keeper.name)
        assertEquals(
            listOf("Long View Rd", "Morriston", "Swansea", "SA6 7JL"),
            result.keeper.addressLines
        )
    }

    @Test
    fun `Given a vehicle with no keeper title, when mapped, the keeper name omits it`() {
        val result = mapper.toUiModel(makeVehicleDetails(keeperTitle = null), dvlaUrls = null)

        assertEquals("DAWN WILLIAMS", result.keeper.name)
    }

    @Test
    fun `Given a vehicle with no keeper names at all, when mapped, the keeper name is blank`() {
        val result = mapper.toUiModel(
            makeVehicleDetails(keeperTitle = null, keeperFirstNames = null, keeperLastName = null),
            dvlaUrls = null
        )

        assertEquals("", result.keeper.name)
    }

    @Test
    fun `Given a vehicle with no keeper address, when mapped, the address lines are empty`() {
        val result = mapper.toUiModel(makeVehicleDetails(keeperFullAddress = null), dvlaUrls = null)

        assertEquals(emptyList<String>(), result.keeper.addressLines)
    }

    @Test
    fun `Given a vehicle, when mapped, make, model and registration are taken from the summary`() {
        val result = mapper.toUiModel(makeVehicleDetails(model = "FIESTA"), dvlaUrls = null)

        assertEquals("FORD", result.make)
        assertEquals("FIESTA", result.model)
        assertEquals("AA19 AAA", result.registration)
    }

    @Test
    fun `Given a vehicle with no value for model, when mapped, model is empty`() {
        val result = mapper.toUiModel(makeVehicleDetails(), dvlaUrls = null)

        assertEquals("", result.model)
    }

    @Test
    fun `Given there is secondary colour, when mapped, colour contains both`() {
        every { stringProvider.getString(R.string.colour_title) } returns "Colour"
        every { stringProvider.getString(R.string.red) } returns "Red"
        every { stringProvider.getString(R.string.blue) } returns "Blue"
        every { stringProvider.getString(R.string.concatenated_vehicle_colours, "Red", "blue") } returns "Red and blue"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.RED, secondaryColour = VehicleColour.BLUE),
            dvlaUrls = null
        )

        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "Colour" }

        assertEquals("Red and blue", colourSpec.info.displayText)
    }

    @Test
    fun `Given secondary colour unknown, when mapped, colour contains only primary colour`() {
        every { stringProvider.getString(R.string.colour_title) } returns "Colour"
        every { stringProvider.getString(R.string.red) } returns "Red"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.RED, secondaryColour = VehicleColour.UNKNOWN),
            dvlaUrls = null
        )

        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "Colour" }

        assertEquals("Red", colourSpec.info.displayText)
    }

    @Test
    fun `Given secondary colour not stated, when mapped, colour contains only primary colour`() {
        every { stringProvider.getString(R.string.colour_title) } returns "Colour"
        every { stringProvider.getString(R.string.red) } returns "Red"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.RED, secondaryColour = VehicleColour.NOT_STATED),
            dvlaUrls = null
        )

        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "Colour" }

        assertEquals("Red", colourSpec.info.displayText)
    }

    @Test
    fun `Given a valid registration date, then mapped date displays as Month Year`() {
        every { stringProvider.getString(R.string.first_registered_title) } returns "First registered"
        every { stringProvider.getString(R.string.first_registered_alt_text, "June 2020") } returns "First registered June 2020"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(dateOfFirstRegistration = LocalDate.of(2020, 6, 1)),
            dvlaUrls = null
        )

        val dateSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "First registered" }

        assertEquals("June 2020", dateSpec.info.displayText)
        assertEquals("", dateSpec.info.altText)
        assertEquals("First registered June 2020", dateSpec.title.altText)
    }

    @Test
    fun `Given valid engine capacity, then mapped engine capacity display text and alt text are correct`() {
        every { stringProvider.getString(R.string.engine_size_title) } returns "Engine size"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(engineCapacity = 2000),
            dvlaUrls = null
        )

        val emissionsSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "Engine size" }

        assertEquals("2000cc", emissionsSpec.info.displayText)
    }

    @Test
    fun `Given engine capacity is NULL, then mapped engine capacity display text and alt text are correct`() {
        every { stringProvider.getString(R.string.engine_size_title) } returns "Engine size"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(engineCapacity = null),
            dvlaUrls = null
        )

        val emissionsSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "Engine size" }

        assertEquals("Unknown", emissionsSpec.info.displayText)
    }

    @Test
    fun `Given valid emissions, then mapped emissions display text and alt text are correct`() {
        every { stringProvider.getString(R.string.emissions_title) } returns "Emissions"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(exhaustEmissionsCo2 = 199),
            dvlaUrls = null
        )

        val emissionsSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "Emissions" }

        assertEquals("199", emissionsSpec.info.displayText)
    }

    @Test
    fun `Given emissions is NULL, then mapped emissions display text and alt text are correct`() {
        every { stringProvider.getString(R.string.emissions_title) } returns "Emissions"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(exhaustEmissionsCo2 = null),
            dvlaUrls = null
        )

        val emissionsSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == "Emissions" }

        assertEquals("Unknown", emissionsSpec.info.displayText)
    }

    @Test
    fun `Given fuel type petrol, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.petrol_summary) } returns "Petrol"
        every { stringProvider.getString(R.string.petrol_specification) } returns "Petrol"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.PETROL),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Petrol", fuelSpecIcon.description.displayText)
        assertEquals("Petrol", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type diesel, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.diesel_summary) } returns "Diesel"
        every { stringProvider.getString(R.string.diesel_specification) } returns "Diesel"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.DIESEL),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Diesel", fuelSpecIcon.description.displayText)
        assertEquals("Diesel", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Electric, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.electric_summary) } returns "Electric"
        every { stringProvider.getString(R.string.electric_specification) } returns "Electric"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.ELECTRICITY),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Electric", fuelSpecIcon.description.displayText)
        assertEquals("Electric", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Steam, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.steam_summary) } returns "Steam"
        every { stringProvider.getString(R.string.steam_specification) } returns "Steam"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.STEAM),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Steam", fuelSpecIcon.description.displayText)
        assertEquals("Steam", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Gas, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.gas_summary) } returns "Gas"
        every { stringProvider.getString(R.string.gas_specification) } returns "Gas"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.GAS),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Gas", fuelSpecIcon.description.displayText)
        assertEquals("Gas", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Petrol Gas, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.petrol_gas_summary) } returns "Bi-fuel"
        every { stringProvider.getString(R.string.petrol_gas_specification) } returns "Petrol and gas"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.PETROL_GAS),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Bi-fuel", fuelSpecIcon.description.displayText)
        assertEquals("Petrol and gas", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Gas Bi-fuel, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.gas_bi_fuel_summary) } returns "Bi-fuel"
        every { stringProvider.getString(R.string.gas_bi_fuel_specification) } returns "Gas bi-fuel"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.GAS_BI_FUEL),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Bi-fuel", fuelSpecIcon.description.displayText)
        assertEquals("Gas bi-fuel", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Hybrid Electric, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.hybrid_electric_summary) } returns "Hybrid"
        every { stringProvider.getString(R.string.hybrid_electric_specification) } returns "Hybrid electric"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.HYBRID_ELECTRIC),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Hybrid", fuelSpecIcon.description.displayText)
        assertEquals("Hybrid electric", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Gas Diesel, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.gas_diesel_summary) } returns "Bi-fuel"
        every { stringProvider.getString(R.string.gas_diesel_specification) } returns "Gas and diesel"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.GAS_DIESEL),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Bi-fuel", fuelSpecIcon.description.displayText)
        assertEquals("Gas and diesel", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Fuel Cells, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.fuel_cells_summary) } returns "Hydrogen"
        every { stringProvider.getString(R.string.fuel_cells_specification) } returns "Hydrogen"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.FUEL_CELLS),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Hydrogen", fuelSpecIcon.description.displayText)
        assertEquals("Hydrogen", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Electric Diesel, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.electric_diesel_summary) } returns "Hybrid"
        every { stringProvider.getString(R.string.electric_diesel_specification) } returns "Electric diesel"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.ELECTRIC_DIESEL),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Hybrid", fuelSpecIcon.description.displayText)
        assertEquals("Electric diesel", fuelSpec.info.displayText)
    }

    @Test
    fun `Given fuel type Other, then mapped fuel type display text are correct`() {
        every { stringProvider.getString(R.string.other) } returns "Other"

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(fuelType = FuelType.OTHER),
            dvlaUrls = null
        )

        val fuelSpecIcon = vehicleDetails.specificationsIcons[1]
        val fuelSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == FUEL_TYPE }

        assertEquals("Other", fuelSpecIcon.description.displayText)
        assertEquals("Other", fuelSpec.info.displayText)
    }

    @Test
    fun `Given colour Brown, then mapped colour display text are correct`() {
        val colour = "Brown"
        every { stringProvider.getString(R.string.brown) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.BROWN),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Bronze, then mapped colour display text are correct`() {
        val colour = "Bronze"
        every { stringProvider.getString(R.string.bronze) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.BRONZE),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Red, then mapped colour display text are correct`() {
        val colour = "Red"
        every { stringProvider.getString(R.string.red) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.RED),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Pink, then mapped colour display text are correct`() {
        val colour = "Pink"
        every { stringProvider.getString(R.string.pink) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.PINK),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Orange, then mapped colour display text are correct`() {
        val colour = "Orange"
        every { stringProvider.getString(R.string.orange) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.ORANGE),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Yellow, then mapped colour display text are correct`() {
        val colour = "Yellow"
        every { stringProvider.getString(R.string.yellow) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.YELLOW),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Gold, then mapped colour display text are correct`() {
        val colour = "Gold"
        every { stringProvider.getString(R.string.gold) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.GOLD),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Green, then mapped colour display text are correct`() {
        val colour = "Green"
        every { stringProvider.getString(R.string.green) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.GREEN),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Blue, then mapped colour display text are correct`() {
        val colour = "Blue"
        every { stringProvider.getString(R.string.blue) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.BLUE),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Purple, then mapped colour display text are correct`() {
        val colour = "Purple"
        every { stringProvider.getString(R.string.purple) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.PURPLE),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Grey, then mapped colour display text are correct`() {
        val colour = "Grey"
        every { stringProvider.getString(R.string.grey) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.GREY),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Silver, then mapped colour display text are correct`() {
        val colour = "Silver"
        every { stringProvider.getString(R.string.silver) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.SILVER),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour White, then mapped colour display text are correct`() {
        val colour = "White"
        every { stringProvider.getString(R.string.white) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.WHITE),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Black, then mapped colour display text are correct`() {
        val colour = "Black"
        every { stringProvider.getString(R.string.black) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.BLACK),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Multi-Colour, then mapped colour display text are correct`() {
        val colour = "Multi-colour"
        every { stringProvider.getString(R.string.multi_colour) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.MULTI_COLOUR),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Beige, then mapped colour display text are correct`() {
        val colour = "Beige"
        every { stringProvider.getString(R.string.beige) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.BEIGE),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Maroon, then mapped colour display text are correct`() {
        val colour = "Maroon"
        every { stringProvider.getString(R.string.maroon) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.MAROON),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Turquoise, then mapped colour display text are correct`() {
        val colour = "Turquoise"
        every { stringProvider.getString(R.string.turquoise) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.TURQUOISE),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Cream, then mapped colour display text are correct`() {
        val colour = "Cream"
        every { stringProvider.getString(R.string.cream) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.CREAM),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }

    @Test
    fun `Given colour Not stated, then mapped colour display text are correct`() {
        val colour = "Not stated"
        every { stringProvider.getString(R.string.not_stated) } returns colour

        val vehicleDetails = mapper.toUiModel(
            makeVehicleDetails(colour = VehicleColour.NOT_STATED),
            dvlaUrls = null
        )

        val colourSpecIcon = vehicleDetails.specificationsIcons[2]
        val colourSpec = vehicleDetails.specifications
            .filterIsInstance<InternalLinkListItemModel.Info>()
            .first { it.title.displayText == COLOUR }

        assertEquals(colour, colourSpecIcon.description.displayText)
        assertEquals(colour, colourSpec.info.displayText)
    }
}