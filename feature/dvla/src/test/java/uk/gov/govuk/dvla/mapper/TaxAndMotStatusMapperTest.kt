package uk.gov.govuk.dvla.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.ui.model.StatusStyle
import uk.gov.govuk.dvla.ui.model.StatusUiModel
import uk.gov.govuk.dvla.ui.model.dvlaUrls
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.getNumberOfDaysFromNow
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
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
        every { stringProvider.getString(R.string.acronym_mot) } returns "MOT"
        every { stringProvider.getString(R.string.acronym_mot_alt_text) } returns "M.O.T."
        every { stringProvider.getString(R.string.tax_status_title) } returns "Tax"
    }

    @Test
    fun `Given the MOT status is VALID and the MOT expiry date is within expiry range, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().plusDays(1)
        val dateToDisplay = date.toSummaryDisplayFormat()
        val daysFromNow = date.getNumberOfDaysFromNow()
        every { stringProvider.getString(R.string.expiring_status_date) } returns "Expiring %1\$s"
        every { stringProvider.getString(R.string.expiring_mot_caption) } returns "It can take a couple of days after your MOT for your status to update"
        every { stringProvider.getString(R.string.expiring_mot_caption_alt_text) } returns "It can take a couple of days after your M.O.T for your status to update"
        every {
            stringProvider.getQuantityString(
                R.plurals.expiring_status_days_left,
                daysFromNow,
                daysFromNow
            )
        } returns "1 day left"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.expiring_status_date,
                dateToDisplay
            )
        } returns "Expiring $dateToDisplay"

        val vehicle = makeVehicle(
            motStatus = MotStatus.VALID,
            motExpiryDate = date
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.CountdownRow)
        motStatus as StatusUiModel.CountdownRow
        assertEquals("MOT", motStatus.countdownBarUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.countdownBarUi.title?.altText)
        assertEquals("Expiring $dateToDisplay", motStatus.countdownBarUi.topText.displayText)
        assertNull(motStatus.countdownBarUi.topText.altText)
        assertEquals("1 day left", motStatus.countdownBarUi.bottomText.displayText)
        assertNull(motStatus.countdownBarUi.bottomText.altText)
        assertTrue(motStatus.countdownBarUi.style is StatusStyle.Caption)
        val style = motStatus.countdownBarUi.style as StatusStyle.Caption
        assertEquals(
            "It can take a couple of days after your MOT for your status to update",
            style.text.displayText
        )
        assertEquals(
            "It can take a couple of days after your M.O.T for your status to update",
            style.text.altText
        )
    }

    @Test
    fun `Given the MOT status is VALID and the MOT expiry date is today, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now()
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.today) } returns "Today"
        every { stringProvider.getString(R.string.expiring_mot_caption) } returns "It can take a couple of days after your MOT for your status to update"
        every { stringProvider.getString(R.string.expiring_mot_caption_alt_text) } returns "It can take a couple of days after your M.O.T for your status to update"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.expiring_status_date,
                dateToDisplay
            )
        } returns "Expiring $dateToDisplay"

        val vehicle = makeVehicle(
            motStatus = MotStatus.VALID,
            motExpiryDate = date
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.CountdownRow)
        motStatus as StatusUiModel.CountdownRow
        assertEquals("MOT", motStatus.countdownBarUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.countdownBarUi.title?.altText)
        assertEquals("Expiring $dateToDisplay", motStatus.countdownBarUi.topText.displayText)
        assertNull(motStatus.countdownBarUi.topText.altText)
        assertEquals("Today", motStatus.countdownBarUi.bottomText.displayText)
        assertNull(motStatus.countdownBarUi.bottomText.altText)
        assertTrue(motStatus.countdownBarUi.style is StatusStyle.Caption)
        val style = motStatus.countdownBarUi.style as StatusStyle.Caption
        assertEquals(
            "It can take a couple of days after your MOT for your status to update",
            style.text.displayText
        )
        assertEquals(
            "It can take a couple of days after your M.O.T for your status to update",
            style.text.altText
        )
    }

    @Test
    fun `Given the MOT status is VALID and the MOT expiry date is outside of the expiry range, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().plusDays(29)
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.valid_until) } returns "Valid until %1\$s"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.valid_until,
                dateToDisplay
            )
        } returns "Valid until $dateToDisplay"

        val vehicle = makeVehicle(
            motStatus = MotStatus.VALID,
            motExpiryDate = date
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.StatusRow)
        motStatus as StatusUiModel.StatusRow
        assertEquals("MOT", motStatus.statusRowUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.statusRowUi.title?.altText)
        assertEquals("Valid until $dateToDisplay", motStatus.statusRowUi.description.displayText)
        assertNull(motStatus.statusRowUi.description.altText)
        assert(motStatus.statusRowUi.iconStyle is StatusListItemIconStyle.Success)
    }

    @Test
    fun `Given the MOT status is VALID and the MOT expiry date is null, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.valid) } returns "Valid"

        val vehicle = makeVehicle(
            motStatus = MotStatus.VALID
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.StatusRow)
        motStatus as StatusUiModel.StatusRow
        assertEquals("MOT", motStatus.statusRowUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.statusRowUi.title?.altText)
        assertEquals("Valid", motStatus.statusRowUi.description.displayText)
        assertNull(motStatus.statusRowUi.description.altText)
        assert(motStatus.statusRowUi.iconStyle is StatusListItemIconStyle.Success)
    }

    @Test
    fun `Given the MOT status is EXPIRED and the MOT expiry date is not NULL, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().minusDays(1)
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.expired_on) } returns "Expired on  %1\$s"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.expired_on,
                dateToDisplay
            )
        } returns "Expired on $dateToDisplay"

        val vehicle = makeVehicle(
            motStatus = MotStatus.EXPIRED,
            motExpiryDate = date
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.StatusRow)
        motStatus as StatusUiModel.StatusRow
        assertEquals("MOT", motStatus.statusRowUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.statusRowUi.title?.altText)
        assertEquals("Expired on $dateToDisplay", motStatus.statusRowUi.description.displayText)
        assertNull(motStatus.statusRowUi.description.altText)
        assert(motStatus.statusRowUi.iconStyle is StatusListItemIconStyle.Warning)
    }

    @Test
    fun `Given the MOT status is EXPIRED and the MOT expiry date is NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.expired) } returns "Expired"

        val vehicle = makeVehicle(
            motStatus = MotStatus.EXPIRED,
            motExpiryDate = null
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.StatusRow)
        motStatus as StatusUiModel.StatusRow
        assertEquals("MOT", motStatus.statusRowUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.statusRowUi.title?.altText)
        assertEquals("Expired", motStatus.statusRowUi.description.displayText)
        assertNull(motStatus.statusRowUi.description.altText)
        assert(motStatus.statusRowUi.iconStyle is StatusListItemIconStyle.Warning)
    }

    @Test
    fun `Given the MOT status is NO DETAILS HELD and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.no_details_held_link_text) } returns "See status on the website"

        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_DETAILS_HELD
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.LinkRow)
        motStatus as StatusUiModel.LinkRow
        assertEquals("MOT", motStatus.linkRowUi.title.displayText)
        assertEquals("M.O.T.", motStatus.linkRowUi.title.altText)
        assertEquals("See status on the website", motStatus.linkRowUi.text.displayText)
        assertNull(motStatus.linkRowUi.text.altText)
        assertEquals(
            "https://www.check-mot.service.gov.uk/results?registration=AA19AAA&checkRecalls=true",
            motStatus.linkRowUi.url.urlToOpen
        )
    }

    @Test
    fun `Given the MOT status is NO DETAILS HELD and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.status_unknown) } returns "Unknown"

        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_DETAILS_HELD
        )
        val motStatus = mapper.getMotStatus(vehicle, null)

        assertTrue(motStatus is StatusUiModel.StatusRow)
        motStatus as StatusUiModel.StatusRow
        assertEquals("MOT", motStatus.statusRowUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.statusRowUi.title?.altText)
        assertEquals("Unknown", motStatus.statusRowUi.description.displayText)
        assertNull(motStatus.statusRowUi.description.altText)
        assertNull(motStatus.statusRowUi.style)
    }

    @Test
    fun `Given the MOT status is NO RESULTS RETURNED and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.check_mot_link_text) } returns "Check if it needs an MOT"
        every { stringProvider.getString(R.string.check_mot_link_alt_text) } returns "Check if it needs an M.O.T."

        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_RESULTS_RETURNED
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.LinkRow)
        motStatus as StatusUiModel.LinkRow
        assertEquals("MOT", motStatus.linkRowUi.title.displayText)
        assertEquals("M.O.T.", motStatus.linkRowUi.title.altText)
        assertEquals("Check if it needs an MOT", motStatus.linkRowUi.text.displayText)
        assertEquals("Check if it needs an M.O.T.", motStatus.linkRowUi.text.altText)
        assertEquals(
            "https://www.gov.uk/historic-vehicles",
            motStatus.linkRowUi.url.urlToOpen
        )
    }

    @Test
    fun `Given the MOT status is NO RESULTS RETURNED and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.status_unknown) } returns "Unknown"

        val vehicle = makeVehicle(
            motStatus = MotStatus.NO_RESULTS_RETURNED
        )
        val motStatus = mapper.getMotStatus(vehicle, null)

        assertTrue(motStatus is StatusUiModel.StatusRow)
        motStatus as StatusUiModel.StatusRow
        assertEquals("MOT", motStatus.statusRowUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.statusRowUi.title?.altText)
        assertEquals("Unknown", motStatus.statusRowUi.description.displayText)
        assertNull(motStatus.statusRowUi.description.altText)
        assertNull(motStatus.statusRowUi.style)
    }

    @Test
    fun `Given the MOT status is UNKNOWN, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.status_unknown) } returns "Unknown"

        val vehicle = makeVehicle(
            motStatus = MotStatus.UNKNOWN
        )
        val motStatus = mapper.getMotStatus(vehicle, dvlaUrls)

        assertTrue(motStatus is StatusUiModel.StatusRow)
        motStatus as StatusUiModel.StatusRow
        assertEquals("MOT", motStatus.statusRowUi.title?.displayText)
        assertEquals("M.O.T.", motStatus.statusRowUi.title?.altText)
        assertEquals("Unknown", motStatus.statusRowUi.description.displayText)
        assertNull(motStatus.statusRowUi.description.altText)
        assertNull(motStatus.statusRowUi.style)
    }

    @Test
    fun `Given the Tax status is TAXED and there is a SORN start date in the future, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().plusDays(1)
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.off_the_road_sorn_message) } returns "Off the road (SORN)"
        every { stringProvider.getString(R.string.sorn_from) } returns "From %s"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.sorn_from,
                dateToDisplay
            )
        } returns "From $dateToDisplay"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            sornStart = date
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.InfoRow)
        taxStatus as StatusUiModel.InfoRow
        assertEquals("Off the road (SORN)", taxStatus.infoRowUi.title.displayText)
        assertNull(taxStatus.infoRowUi.title.altText)
        assertEquals("From $dateToDisplay", taxStatus.infoRowUi.subtitle?.displayText)
        assertNull(taxStatus.infoRowUi.subtitle?.altText)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is within expiry range and the payment method is DD, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().plusDays(1)
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.paying_by_direct_debit) } returns "Paying by Direct Debit"
        every { stringProvider.getString(R.string.expiring_direct_debit_status_date) } returns "Renews %1\$s"
        every { stringProvider.getString(R.string.manage_payment_button) } returns "Manage payment"
        every { stringProvider.getString(R.string.manage_payment_button_caption) } returns "It can take a couple of days after you’ve paid for your status to update"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.expiring_direct_debit_status_date,
                dateToDisplay
            )
        } returns "Renews $dateToDisplay"
        every {
            stringProvider.getQuantityString(
                R.plurals.expiring_status_days_left,
                date.getNumberOfDaysFromNow(),
                date.getNumberOfDaysFromNow()
            )
        } returns "1 day left"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = date,
            currentLicencePaymentMethod = "Direct Debit"
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.CountdownRow)
        taxStatus as StatusUiModel.CountdownRow
        assertEquals("Tax", taxStatus.countdownBarUi.title?.displayText)
        assertNull(taxStatus.countdownBarUi.title?.altText)
        assertEquals("Renews $dateToDisplay", taxStatus.countdownBarUi.topText.displayText)
        assertNull(taxStatus.countdownBarUi.topText.altText)
        assertEquals("Paying by Direct Debit", taxStatus.countdownBarUi.bottomText.displayText)
        assertNull(taxStatus.countdownBarUi.bottomText.altText)
        assertTrue(taxStatus.countdownBarUi.style is StatusStyle.ActionButton)
        val style = taxStatus.countdownBarUi.style as StatusStyle.ActionButton
        assertEquals("Manage payment", style.text.displayText)
        assertNull(style.text.altText)
        assertEquals(
            "It can take a couple of days after you’ve paid for your status to update",
            style.caption?.displayText
        )
        assertNull(style.caption?.altText)
        assertEquals("https://www.gov.uk/vehicle-tax-direct-debit/renewing", style.url.urlToOpen)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is within expiry range and the payment method is not DD, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().plusDays(1)
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.expiring_status_date) } returns "Expiring %1\$s"
        every { stringProvider.getString(R.string.renew_tax_button) } returns "Renew tax"
        every { stringProvider.getString(R.string.renew_tax_button_caption) } returns "It can take a couple of days after you’ve paid for your status to update"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.expiring_status_date,
                dateToDisplay
            )
        } returns "Expiring $dateToDisplay"
        every {
            stringProvider.getQuantityString(
                R.plurals.expiring_status_days_left,
                date.getNumberOfDaysFromNow(),
                date.getNumberOfDaysFromNow()
            )
        } returns "1 day left"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = date
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.CountdownRow)
        taxStatus as StatusUiModel.CountdownRow
        assertEquals("Tax", taxStatus.countdownBarUi.title?.displayText)
        assertNull(taxStatus.countdownBarUi.title?.altText)
        assertEquals("Expiring $dateToDisplay", taxStatus.countdownBarUi.topText.displayText)
        assertNull(taxStatus.countdownBarUi.topText.altText)
        assertEquals("1 day left", taxStatus.countdownBarUi.bottomText.displayText)
        assertNull(taxStatus.countdownBarUi.bottomText.altText)
        assertTrue(taxStatus.countdownBarUi.style is StatusStyle.ActionButton)
        val style = taxStatus.countdownBarUi.style as StatusStyle.ActionButton
        assertEquals("Renew tax", style.text.displayText)
        assertNull(style.text.altText)
        assertEquals(
            "It can take a couple of days after you’ve paid for your status to update",
            style.caption?.displayText
        )
        assertNull(style.caption?.altText)
        assertEquals("https://www.gov.uk/vehicle-tax", style.url.urlToOpen)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is today and the payment method is not DD, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now()
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.today) } returns "Today"
        every { stringProvider.getString(R.string.expiring_status_date) } returns "Expiring %1\$s"
        every { stringProvider.getString(R.string.renew_tax_button) } returns "Renew tax"
        every { stringProvider.getString(R.string.renew_tax_button_caption) } returns "It can take a couple of days after you’ve paid for your status to update"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.expiring_status_date,
                dateToDisplay
            )
        } returns "Expiring $dateToDisplay"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = date
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.CountdownRow)
        taxStatus as StatusUiModel.CountdownRow
        assertEquals("Tax", taxStatus.countdownBarUi.title?.displayText)
        assertNull(taxStatus.countdownBarUi.title?.altText)
        assertEquals("Expiring $dateToDisplay", taxStatus.countdownBarUi.topText.displayText)
        assertNull(taxStatus.countdownBarUi.topText.altText)
        assertEquals("Today", taxStatus.countdownBarUi.bottomText.displayText)
        assertNull(taxStatus.countdownBarUi.bottomText.altText)
        assertTrue(taxStatus.countdownBarUi.style is StatusStyle.ActionButton)
        val style = taxStatus.countdownBarUi.style as StatusStyle.ActionButton
        assertEquals("Renew tax", style.text.displayText)
        assertNull(style.text.altText)
        assertEquals(
            "It can take a couple of days after you’ve paid for your status to update",
            style.caption?.displayText
        )
        assertNull(style.caption?.altText)
        assertEquals("https://www.gov.uk/vehicle-tax", style.url.urlToOpen)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is outside of the expiry range, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().plusDays(29)
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.valid_until) } returns "Valid until %1\$s"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.valid_until,
                dateToDisplay
            )
        } returns "Valid until $dateToDisplay"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = date
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.StatusRow)
        taxStatus as StatusUiModel.StatusRow
        assertEquals("Tax", taxStatus.statusRowUi.title?.displayText)
        assertNull(taxStatus.statusRowUi.title?.altText)
        assertEquals("Valid until $dateToDisplay", taxStatus.statusRowUi.description.displayText)
        assertNull(taxStatus.statusRowUi.description.altText)
        assert(taxStatus.statusRowUi.iconStyle is StatusListItemIconStyle.Success)
    }

    @Test
    fun `Given the Tax status is TAXED, the tax expiry date is NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.valid) } returns "Valid"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.TAXED,
            taxExpiryDate = null
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.StatusRow)
        taxStatus as StatusUiModel.StatusRow
        assertEquals("Tax", taxStatus.statusRowUi.title?.displayText)
        assertNull(taxStatus.statusRowUi.title?.altText)
        assertEquals("Valid", taxStatus.statusRowUi.description.displayText)
        assertNull(taxStatus.statusRowUi.description.altText)
        assert(taxStatus.statusRowUi.iconStyle is StatusListItemIconStyle.Success)
    }

    @Test
    fun `Given the Tax status is UNTAXED and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.untaxed) } returns "Untaxed"
        every { stringProvider.getString(R.string.renew_tax_button) } returns "Renew tax"
        every { stringProvider.getString(R.string.renew_tax_button_caption) } returns "It can take a couple of days after you’ve paid for your status to update"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNTAXED
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.StatusRow)
        taxStatus as StatusUiModel.StatusRow
        assertEquals("Tax", taxStatus.statusRowUi.title?.displayText)
        assertNull(taxStatus.statusRowUi.title?.altText)
        assertEquals("Untaxed", taxStatus.statusRowUi.description.displayText)
        assertNull(taxStatus.statusRowUi.description.altText)
        assertTrue(taxStatus.statusRowUi.style is StatusStyle.ActionButton)
        val style = taxStatus.statusRowUi.style as StatusStyle.ActionButton
        assertEquals("Renew tax", style.text.displayText)
        assertNull(style.text.altText)
        assertEquals(
            "It can take a couple of days after you’ve paid for your status to update",
            style.caption?.displayText
        )
        assertNull(style.caption?.altText)
        assertEquals("https://www.gov.uk/vehicle-tax", style.url.urlToOpen)
    }

    @Test
    fun `Given the Tax status is UNTAXED and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.untaxed) } returns "Untaxed"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNTAXED
        )
        val taxStatus = mapper.getTaxStatus(vehicle, null)

        assertTrue(taxStatus is StatusUiModel.StatusRow)
        taxStatus as StatusUiModel.StatusRow
        assertEquals("Tax", taxStatus.statusRowUi.title?.displayText)
        assertNull(taxStatus.statusRowUi.title?.altText)
        assertEquals("Untaxed", taxStatus.statusRowUi.description.displayText)
        assertNull(taxStatus.statusRowUi.description.altText)
        assertNull(taxStatus.statusRowUi.style)
    }

    @Test
    fun `Given the Tax status is SORN and SORN start date is in the future, then the correct StatusUiModel is returned`() {
        val date = LocalDate.now().plusDays(1)
        val dateToDisplay = date.toSummaryDisplayFormat()
        every { stringProvider.getString(R.string.off_the_road_sorn_message) } returns "Off the road (SORN)"
        every { stringProvider.getString(R.string.sorn_from) } returns "From %s"
        every {
            stringProvider.resolveSummaryDescription(
                R.string.sorn_from,
                dateToDisplay
            )
        } returns "From $dateToDisplay"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.SORN,
            sornStart = date
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.InfoRow)
        taxStatus as StatusUiModel.InfoRow
        assertEquals("Off the road (SORN)", taxStatus.infoRowUi.title.displayText)
        assertNull(taxStatus.infoRowUi.title.altText)
        assertEquals("From $dateToDisplay", taxStatus.infoRowUi.subtitle?.displayText)
        assertNull(taxStatus.infoRowUi.subtitle?.altText)
    }

    @Test
    fun `Given the Tax status is SORN and SORN start date is not in the future, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.off_the_road_sorn_message) } returns "Off the road (SORN)"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.SORN,
            sornStart = LocalDate.now()
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.InfoRow)
        taxStatus as StatusUiModel.InfoRow
        assertEquals("Off the road (SORN)", taxStatus.infoRowUi.title.displayText)
        assertNull(taxStatus.infoRowUi.title.altText)
        assertNull(taxStatus.infoRowUi.subtitle)
    }

    @Test
    fun `Given the Tax status is NOT TAXED FOR ON ROAD USE, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.status_not_needed) } returns "No tax to pay"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.StatusRow)
        taxStatus as StatusUiModel.StatusRow
        assertEquals("Tax", taxStatus.statusRowUi.title?.displayText)
        assertNull(taxStatus.statusRowUi.title?.altText)
        assertEquals("No tax to pay", taxStatus.statusRowUi.description.displayText)
        assertNull(taxStatus.statusRowUi.description.altText)
        assertNull(taxStatus.statusRowUi.style)
    }

    @Test
    fun `Given the Tax status is UNKNOWN and DVLA url's is not NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.tax_status_unknown) } returns "Not found - contact DVLA"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNKNOWN
        )
        val taxStatus = mapper.getTaxStatus(vehicle, dvlaUrls)

        assertTrue(taxStatus is StatusUiModel.LinkRow)
        taxStatus as StatusUiModel.LinkRow
        assertEquals("Tax", taxStatus.linkRowUi.title.displayText)
        assertNull(taxStatus.linkRowUi.title.altText)
        assertEquals("Not found - contact DVLA", taxStatus.linkRowUi.text.displayText)
        assertNull(taxStatus.linkRowUi.text.altText)
        assertEquals(
            "https://www.gov.uk/contact-the-dvla",
            taxStatus.linkRowUi.url.urlToOpen
        )
    }

    @Test
    fun `Given the Tax status is UNKNOWN and DVLA url's is NULL, then the correct StatusUiModel is returned`() {
        every { stringProvider.getString(R.string.status_unknown) } returns "Unknown"

        val vehicle = makeVehicle(
            taxStatus = TaxStatus.UNKNOWN
        )
        val taxStatus = mapper.getTaxStatus(vehicle, null)

        assertTrue(taxStatus is StatusUiModel.StatusRow)
        taxStatus as StatusUiModel.StatusRow
        assertEquals("Tax", taxStatus.statusRowUi.title?.displayText)
        assertNull(taxStatus.statusRowUi.title?.altText)
        assertEquals("Unknown", taxStatus.statusRowUi.description.displayText)
        assertNull(taxStatus.statusRowUi.description.altText)
        assertNull(taxStatus.statusRowUi.style)
    }
}
