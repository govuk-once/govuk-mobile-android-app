package uk.gov.govuk.dvla.ui.model

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

    @Before
    fun setup() {
        every { stringProvider.getString(any<Int>(), *anyVararg()) } returns ""
    }

    private fun makeVehicleDetails(
        keeperTitle: String? = "MR",
        keeperFirstNames: String? = "DAWN",
        keeperLastName: String? = "WILLIAMS",
        keeperFullAddress: String? = "Long View Rd\nMorriston\nSwansea\nSA6 7JL",
        colour: VehicleColour = VehicleColour.RED,
        secondaryColour: VehicleColour? = null,
        dateOfFirstRegistration: LocalDate? = LocalDate.of(2020, 6, 1),
        exhaustEmissionsCo2: Int? = 199,
        model: String? = null
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
        fuelType = FuelType.PETROL,
        colour = colour,
        secondaryColour = secondaryColour,
        engineCapacity = 2000,
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
}
