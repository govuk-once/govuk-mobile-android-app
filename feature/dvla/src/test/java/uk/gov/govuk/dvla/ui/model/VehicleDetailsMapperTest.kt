package uk.gov.govuk.dvla.ui.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
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
        keeperFullAddress: String? = "Long View Rd\nMorriston\nSwansea\nSA6 7JL"
    ) = VehicleDetails(
        summary = VehicleSummary(
            vehicleId = 156487251,
            registration = "AA19 AAA",
            make = "FORD",
            model = "FIESTA",
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = null,
            motStatus = MotStatus.VALID,
            motExpiryDate = null,
            sornStart = null,
            currentLicencePaymentMethod = null
        ),
        dateOfFirstRegistration = LocalDate.of(2020, 6, 1),
        fuelType = FuelType.PETROL,
        colour = VehicleColour.RED,
        secondaryColour = null,
        engineCapacity = 2000,
        exhaustEmissionsCo2 = 199,
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
        val result = mapper.toUiModel(makeVehicleDetails(), dvlaUrls = null)

        assertEquals("FORD", result.make)
        assertEquals("FIESTA", result.model)
        assertEquals("AA19 AAA", result.registration)
    }
}
