package uk.gov.govuk.dvla.ui.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.util.StringProvider
import java.time.LocalDate

class VehicleSummaryMapperTest {

    private val stringProvider = mockk<StringProvider>()
    private val mapper = VehicleSummaryMapper(stringProvider)

    private val dvlaUrls = DvlaUrls(
        addVehicle = "https://add-vehicle",
        renewLicence = "https://renew-licence",
        soldVehicle = "https://sold-vehicle",
        sornRules = "https://sorn-rules",
        makeSorn = "https://make-sorn",
        getLogbook = "https://get-logbook",
        changeLogbookAddress = "https://change-logbook-address",
        cancelTax = "https://cancel-tax",
        changeLicenceAddress = "https://www.gov.uk/change-licence-address",
        changeNameGenderLicence = "https://www.gov.uk/change-name-gender-licence",
        replaceLicence = "https://www.gov.uk/replace-licence",
        manageTaxPayment = "https://www.gov.uk/vehicle-tax-direct-debit/renewing",
        taxVehicle = "https://www.gov.uk/vehicle-tax"
    )

    @Before
    fun setup() {
        every { stringProvider.getString(any<Int>(), *anyVararg()) } returns ""
    }

    private fun makeVehicle(
        sornStart: LocalDate? = null,
        taxStatus: TaxStatus = TaxStatus.UNKNOWN
    ) = mockk<CustomerVehicle> {
        every { registration } returns "AA19 AAA"
        every { make } returns "FORD"
        every { model } returns "FIESTA"
        every { this@mockk.taxStatus } returns taxStatus
        every { motStatus } returns MotStatus.UNKNOWN
        every { taxExpiryDate } returns null
        every { motExpiryDate } returns null
        every { this@mockk.sornStart } returns sornStart
    }

    @Test
    fun `Given dvlaUrls is null, then menu items is empty`() {
        val result = mapper.toUiModel(makeVehicle(), dvlaUrls = null)
        assertTrue(result.menuItems.isEmpty())
    }

    @Test
    fun `Given vehicle has no SORN, then menu contains Register as off road`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = null), dvlaUrls)
        assertTrue(result.menuItems.any { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.makeSorn })
    }

    @Test
    fun `Given vehicle has no SORN, then menu does not contain SORN rules`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = null), dvlaUrls)
        assertTrue(result.menuItems.none { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.sornRules })
    }

    @Test
    fun `Given vehicle has SORN, then menu contains SORN rules`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = LocalDate.of(2025, 1, 1)), dvlaUrls)
        assertTrue(result.menuItems.any { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.sornRules })
    }

    @Test
    fun `Given vehicle has SORN, then menu does not contain Register as off road`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = LocalDate.of(2025, 1, 1)), dvlaUrls)
        assertTrue(result.menuItems.none { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.makeSorn })
    }

    @Test
    fun `Given dvlaUrls is non-null, then common menu items are always present`() {
        listOf(null, LocalDate.of(2025, 1, 1)).forEach { sornStart ->
            val result = mapper.toUiModel(makeVehicle(sornStart = sornStart), dvlaUrls)
            val urls = result.menuItems.map { (it.action as? MenuAction.WebLink)?.url }
            assertTrue(urls.contains(dvlaUrls.soldVehicle))
            assertTrue(urls.contains(dvlaUrls.getLogbook))
            assertTrue(urls.contains(dvlaUrls.changeLogbookAddress))
        }
    }

    @Test
    fun `Given vehicle has no SORN then the make sorn menu item is present and the sorn rules menu item is not present`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = null), dvlaUrls)
        val urls = result.menuItems.map { (it.action as MenuAction.WebLink).url }
        assertTrue(urls.contains(dvlaUrls.makeSorn))
        assertFalse(urls.contains(dvlaUrls.sornRules))
    }

    @Test
    fun `Given vehicle has SORN then the make sorn menu item is not present and the sorn rules menu item is present`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = LocalDate.of(2026, 1, 1)), dvlaUrls)
        val urls = result.menuItems.map { (it.action as MenuAction.WebLink).url }
        assertTrue(urls.contains(dvlaUrls.sornRules))
        assertFalse(urls.contains(dvlaUrls.makeSorn))
    }

    @Test
    fun `Given vehicle is taxed then the cancel tax button is present`() {
        val result = mapper.toUiModel(makeVehicle(taxStatus = TaxStatus.TAXED), dvlaUrls)
        val urls = result.menuItems.map { (it.action as MenuAction.WebLink).url }
        assertTrue(urls.contains(dvlaUrls.cancelTax))
    }

    @Test
    fun `Given vehicle is not taxed then the cancel tax button is not present`() {
        val result = mapper.toUiModel(makeVehicle(taxStatus = TaxStatus.UNTAXED), dvlaUrls)
        val urls = result.menuItems.map { (it.action as MenuAction.WebLink).url }
        assertFalse(urls.contains(dvlaUrls.cancelTax))
    }
}
