package uk.gov.govuk.dvla.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.ui.model.StatusStyle
import uk.gov.govuk.dvla.ui.model.StatusUiModel
import uk.gov.govuk.dvla.ui.model.dvlaUrls
import uk.gov.govuk.dvla.util.StringProvider
import java.time.LocalDate

class TaxAndMotStatusMapperTest {
    private val stringProvider = mockk<StringProvider>()
    private val mapper = TaxAndMotStatusMapper(stringProvider)

    private fun makeVehicle(
        model: String? = null,
        sornStart: LocalDate? = null,
        motStatus: MotStatus = MotStatus.UNKNOWN,
        motExpiryDate: LocalDate? = null,
        taxStatus: TaxStatus = TaxStatus.UNKNOWN,
        taxExpiryDate: LocalDate? = null,
        currentLicencePaymentMethod: String? = null
    ) = VehicleSummary(
        vehicleId = 156487251,
        registration = "AA19 AAA",
        make = "FORD",
        model = model,
        taxStatus = taxStatus,
        taxExpiryDate = taxExpiryDate,
        motStatus = motStatus,
        motExpiryDate = motExpiryDate,
        sornStart = sornStart,
        currentLicencePaymentMethod = currentLicencePaymentMethod
    )

    @Before
    fun setup() {
        every { stringProvider.getString(any<Int>(), *anyVararg()) } returns ""
        every { stringProvider.getQuantityString(any(), any(), *anyVararg()) } returns ""
    }

    @Test
    fun `Given the MOT status is VALID and the MOT expiry date is within expiry range, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.VALID,
            motExpiryDate = LocalDate.now().plusDays(1)
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)
        assertTrue(motStatus is StatusUiModel.CountdownRow)
    }

    @Test
    fun `Given the MOT status is VALID and the MOT expiry date is outside of the expiry range, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.VALID,
            motExpiryDate = LocalDate.now().plusDays(29)
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)
        assertTrue(motStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the MOT status is VALID and the MOT expiry date is null, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.VALID
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)
        assertTrue(motStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the MOT status is EXPIRED and the MOT expiry date is within expiry range, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.EXPIRED
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)
        assertTrue(motStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the MOT status is NO DETAILS HELD and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_DETAILS_HELD
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)
        assertTrue(motStatus is StatusUiModel.LinkRow)
    }

    @Test
    fun `Given the MOT status is NO DETAILS HELD and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_DETAILS_HELD
        )
        val motStatus = mapper.getMotStatus(vehicle, null)
        assertTrue(motStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the MOT status is NO RESULTS RETURNED and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_RESULTS_RETURNED
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)
        assertTrue(motStatus is StatusUiModel.LinkRow)
    }

    @Test
    fun `Given the MOT status is NO RESULTS RETURNED and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_RESULTS_RETURNED
        )
        val motStatus = mapper.getMotStatus(vehicle, null)
        assertTrue(motStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the MOT status is UNKNOWN, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            motStatus = MotStatus.UNKNOWN
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)
        assertTrue(motStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the Tax status is TAXED and there is a SORN start date in the future, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            sornStart = LocalDate.now().plusDays(1)
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.InfoRow)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is within expiry range and the payment method is DD, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = LocalDate.now().plusDays(1),
            currentLicencePaymentMethod = "Direct Debit"
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.CountdownRow)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is within expiry range and the payment method is not DD, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = LocalDate.now().plusDays(1)
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.CountdownRow)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is outside of the expiry range, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = LocalDate.now().plusDays(29)
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the Tax status is UNTAXED and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNTAXED
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.StatusRow)
        assertTrue((taxStatus as StatusUiModel.StatusRow).statusRowUi.style is StatusStyle.ActionButton)
    }

    @Test
    fun `Given the Tax status is UNTAXED and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNTAXED
        )
        val taxStatus = mapper.getTaxStatus(vehicle, null)
        assertTrue(taxStatus is StatusUiModel.StatusRow)
        assertNull((taxStatus as StatusUiModel.StatusRow).statusRowUi.style)
    }

    @Test
    fun `Given the Tax status is SORN and SORN start date is in the future, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.SORN,
            sornStart = LocalDate.now().plusDays(1)
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.InfoRow)
        assertNotNull((taxStatus as StatusUiModel.InfoRow).infoRowUi.subtitle)
    }

    @Test
    fun `Given the Tax status is SORN and SORN start date is not in the future, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.SORN,
            sornStart = LocalDate.now()
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.InfoRow)
        assertNull((taxStatus as StatusUiModel.InfoRow).infoRowUi.subtitle)
    }

    @Test
    fun `Given the Tax status is NOT TAXED FOR ON ROAD USE, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.StatusRow)
    }

    @Test
    fun `Given the Tax status is UNKNOWN and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNKNOWN
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)
        assertTrue(taxStatus is StatusUiModel.LinkRow)
    }

    @Test
    fun `Given the Tax status is UNKNOWN and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNKNOWN
        )
        val taxStatus = mapper.getTaxStatus(vehicle, null)
        assertTrue(taxStatus is StatusUiModel.StatusRow)
    }
}
