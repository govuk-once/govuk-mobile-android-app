package uk.gov.govuk.dvla.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.FuelType
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleColour
import uk.gov.govuk.dvla.domain.VehicleEnquiryDetails
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.StatusUiModel
import uk.gov.govuk.dvla.ui.model.dvlaUrls
import uk.gov.govuk.dvla.util.StringProvider
import java.time.LocalDate

class CheckVehicleDetailsMapperTest {

    private val stringProvider = mockk<StringProvider>(relaxed = true)

    private val mapper = CheckVehicleDetailsMapper(
        stringProvider,
        TaxAndMotStatusMapper(stringProvider),
        SpecificationsMapper(stringProvider)
    )

    private companion object {
        const val MODEL_TITLE = "Model"
        const val REPORT_ON_THE_ROAD = "Report as on the road"
        const val REPORT_ON_THE_ROAD_ALT = "Report vehicle as on the road"
    }

    @Before
    fun setup() {
        every { stringProvider.getString(R.string.model_title) } returns MODEL_TITLE
        every { stringProvider.getString(R.string.menu_report_as_on_the_road) } returns REPORT_ON_THE_ROAD
        every { stringProvider.getString(R.string.menu_report_as_on_the_road_alt_text) } returns REPORT_ON_THE_ROAD_ALT
    }

    private fun makeEnquiry(
        taxStatus: TaxStatus = TaxStatus.TAXED,
        taxExpiryDate: LocalDate? = null,
        motStatus: MotStatus = MotStatus.VALID,
        motExpiryDate: LocalDate? = null
    ) = VehicleEnquiryDetails(
        vehicleId = 1,
        registration = "AB12CDE",
        make = "FORD",
        taxStatus = taxStatus,
        taxExpiryDate = taxExpiryDate,
        motStatus = motStatus,
        motExpiryDate = motExpiryDate,
        dateOfFirstRegistration = LocalDate.of(2020, 6, 1),
        fuelType = FuelType.PETROL,
        colour = VehicleColour.RED,
        secondaryColour = null,
        engineCapacity = 2000,
        exhaustEmissionsCo2 = 199
    )

    private fun menuUrls(taxStatus: TaxStatus, motStatus: MotStatus) =
        mapper.toUiModel(makeEnquiry(taxStatus = taxStatus, motStatus = motStatus), dvlaUrls)
            .menuItems.map { (it.action as MenuAction.WebLink).url }

    @Test
    fun `When mapped, then make and registration are set and model and keeper are omitted`() {
        val result = mapper.toUiModel(makeEnquiry(), dvlaUrls)

        assertEquals("FORD", result.make)
        assertEquals("AB12CDE", result.registration)
        assertNull(result.model)
        assertNull(result.keeper)
    }

    @Test
    fun `When mapped, then specifications do not include model`() {
        val result = mapper.toUiModel(makeEnquiry(), dvlaUrls)

        assertTrue(
            result.specifications
                .filterIsInstance<InternalLinkListItemModel.Info>()
                .none { it.title.displayText == MODEL_TITLE }
        )
    }

    @Test
    fun `Given tax and MOT expiring soon, when mapped, then no countdowns are shown`() {
        val soon = LocalDate.now().plusDays(10)

        val result = mapper.toUiModel(
            makeEnquiry(taxExpiryDate = soon, motExpiryDate = soon),
            dvlaUrls
        )

        assertTrue(result.taxStatus is StatusUiModel.StatusRow)
        assertTrue(result.motStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given dvlaUrls is null, then menu items is empty`() {
        val result = mapper.toUiModel(makeEnquiry(), dvlaUrls = null)

        assertTrue(result.menuItems.isEmpty())
    }

    @Test
    fun `Given dvlaUrls is non-null, then common menu items are always present`() {
        val urls = menuUrls(TaxStatus.TAXED, MotStatus.VALID)

        assertTrue(urls.contains(dvlaUrls.soldVehicle))
        assertTrue(urls.contains(dvlaUrls.buyingUsedCarChecks))
        assertTrue(urls.contains(dvlaUrls.reportAbandonedVehicle))
    }

    @Test
    fun `Given MOT valid and not taxed, then report on the road links to report untaxed vehicle`() {
        listOf(TaxStatus.UNTAXED, TaxStatus.SORN, TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE).forEach { taxStatus ->
            val urls = menuUrls(taxStatus, MotStatus.VALID)

            assertTrue(urls.contains(dvlaUrls.reportUntaxedVehicle))
            assertFalse(urls.contains(dvlaUrls.reportNoMot))
        }
    }

    @Test
    fun `Given MOT not valid and taxed, then report on the road links to report no MOT`() {
        val urls = menuUrls(TaxStatus.TAXED, MotStatus.EXPIRED)

        assertTrue(urls.contains(dvlaUrls.reportNoMot))
        assertFalse(urls.contains(dvlaUrls.reportUntaxedVehicle))
    }

    @Test
    fun `Given MOT not valid and not taxed, then report on the road links to report no MOT`() {
        listOf(TaxStatus.UNTAXED, TaxStatus.SORN, TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE).forEach { taxStatus ->
            val urls = menuUrls(taxStatus, MotStatus.EXPIRED)

            assertTrue(urls.contains(dvlaUrls.reportNoMot))
            assertFalse(urls.contains(dvlaUrls.reportUntaxedVehicle))
        }
    }

    @Test
    fun `Given any other combination, then report on the road is not shown`() {
        listOf(
            TaxStatus.TAXED to MotStatus.VALID,
            TaxStatus.UNKNOWN to MotStatus.VALID,
            TaxStatus.UNKNOWN to MotStatus.EXPIRED,
            TaxStatus.UNTAXED to MotStatus.NO_DETAILS_HELD,
            TaxStatus.UNTAXED to MotStatus.UNKNOWN
        ).forEach { (taxStatus, motStatus) ->
            val urls = menuUrls(taxStatus, motStatus)

            assertFalse(urls.contains(dvlaUrls.reportUntaxedVehicle))
            assertFalse(urls.contains(dvlaUrls.reportNoMot))
        }
    }
}