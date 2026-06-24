package uk.gov.govuk.dvla.ui.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.util.StringProvider

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
        cancelTax = "https://cancel-tax"
    )

    @Before
    fun setup() {
        every { stringProvider.getString(any<Int>()) } returns ""
    }

    private fun makeVehicle(sornStart: String? = null) = mockk<CustomerVehicle> {
        every { registration } returns "AA19 AAA"
        every { make } returns "FORD"
        every { model } returns "FIESTA"
        every { taxStatus } returns TaxStatus.UNKNOWN
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
        assertTrue(result.menuItems.any { it.url == dvlaUrls.makeSorn })
    }

    @Test
    fun `Given vehicle has no SORN, then menu does not contain SORN rules`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = null), dvlaUrls)
        assertTrue(result.menuItems.none { it.url == dvlaUrls.sornRules })
    }

    @Test
    fun `Given vehicle has SORN, then menu contains SORN rules`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = "2025-01-01"), dvlaUrls)
        assertTrue(result.menuItems.any { it.url == dvlaUrls.sornRules })
    }

    @Test
    fun `Given vehicle has SORN, then menu does not contain Register as off road`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = "2025-01-01"), dvlaUrls)
        assertTrue(result.menuItems.none { it.url == dvlaUrls.makeSorn })
    }

    @Test
    fun `Given dvlaUrls is non-null, then common menu items are always present`() {
        listOf(null, "2025-01-01").forEach { sornStart ->
            val result = mapper.toUiModel(makeVehicle(sornStart = sornStart), dvlaUrls)
            val urls = result.menuItems.map { it.url }
            assertTrue(urls.contains(dvlaUrls.soldVehicle))
            assertTrue(urls.contains(dvlaUrls.getLogbook))
            assertTrue(urls.contains(dvlaUrls.changeLogbookAddress))
            assertTrue(urls.contains(dvlaUrls.cancelTax))
        }
    }

    @Test
    fun `Given vehicle has no SORN, menu contains 5 items`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = null), dvlaUrls)
        assertEquals(5, result.menuItems.size)
    }

    @Test
    fun `Given vehicle has SORN, menu contains 5 items`() {
        val result = mapper.toUiModel(makeVehicle(sornStart = "2025-01-01"), dvlaUrls)
        assertEquals(5, result.menuItems.size)
    }
}
