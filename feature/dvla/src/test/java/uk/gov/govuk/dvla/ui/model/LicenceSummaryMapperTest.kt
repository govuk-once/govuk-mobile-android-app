package uk.gov.govuk.dvla.ui.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.component.error.ErrorConstants.GOV_UK_URL
import uk.gov.govuk.dvla.domain.LicenceDetails
import uk.gov.govuk.dvla.domain.LicenceStatus
import uk.gov.govuk.dvla.domain.LicenceType
import uk.gov.govuk.dvla.util.StringProvider
import java.time.LocalDate

class LicenceSummaryMapperTest {

    private val stringProvider = mockk<StringProvider>()
    private val mapper = LicenceSummaryMapper(stringProvider)

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
        taxVehicle = "https://www.gov.uk/vehicle-tax",
        historicVehicles = "https://www.gov.uk/historic-vehicles",
        checkMot = "https://www.check-mot.service.gov.uk/results?registration=[NUMBER PLATE]&checkRecalls=true",
        driverDetails = "https://driver-and-vehicles-account.service.gov.uk/driver_details",
        account = "https://driver-and-vehicles-account.service.gov.uk",
        drivingRecord = "https://driver-and-vehicles-account.service.gov.uk/driver_details?locale=en#Entitlements"
    )

    @Before
    fun setup() {
        every { stringProvider.getString(any<Int>(), *anyVararg()) } returns ""
        every { stringProvider.getQuantityString(any(), any(), *anyVararg()) } returns ""
    }

    private fun makeLicenceDetails(
        status: LicenceStatus,
        expiryDate: LocalDate? = LocalDate.now().plusYears(1)
    ) = LicenceDetails(
        licenceType = LicenceType.FULL,
        drivingLicenceNumber = "ARENO803236AA170",
        driverTitle = "Ms",
        driverFirstNames = "Anna Ornella",
        driverLastName = "Areno",
        driverFullAddress = "29 Orchard Drive\nMilton Keynes\nPA98 J83",
        tokenValidToDate = expiryDate,
        licenceStatus = status
    )

    private fun successLicence(status: LicenceStatus, expiryDate: LocalDate? = LocalDate.now().plusYears(1)) =
        (mapper.toUiModel(makeLicenceDetails(status, expiryDate), dvlaUrls) as LicenceSummaryUiState.Success).licence

    @Test
    fun `Given licence status is valid, then result is Success and drivingRecordUrl is populated from dvlaUrls`() {
        val licence = successLicence(status = LicenceStatus.VALID)
        assertEquals(dvlaUrls.drivingRecord, licence.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is valid and expiring soon, then result is Success and drivingRecordUrl is still populated`() {
        val licence = successLicence(status = LicenceStatus.VALID, expiryDate = LocalDate.now().plusDays(10))
        assertEquals(dvlaUrls.drivingRecord, licence.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is valid and expiry date is in the past, then result is Success and drivingRecordUrl is still populated`() {
        val licence = successLicence(status = LicenceStatus.VALID, expiryDate = LocalDate.now().minusDays(10))
        assertEquals(dvlaUrls.drivingRecord, licence.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is expired, then result is Success and drivingRecordUrl is null`() {
        val licence = successLicence(status = LicenceStatus.EXPIRED)
        assertNull(licence.drivingRecordUrl)
    }

    @Test
    fun `Given dvlaUrls is null, then result is Success and drivingRecordUrl is null even when licence is valid`() {
        val result = mapper.toUiModel(makeLicenceDetails(status = LicenceStatus.VALID), dvlaUrls = null)
        assertTrue(result is LicenceSummaryUiState.Success)
        assertNull((result as LicenceSummaryUiState.Success).licence.drivingRecordUrl)
    }

    @Test
    fun `Given dvlaUrls drivingRecord is blank, then result is Success and drivingRecordUrl is null even when licence is valid`() {
        val blankUrls = dvlaUrls.copy(drivingRecord = "")
        val licence = (mapper.toUiModel(makeLicenceDetails(status = LicenceStatus.VALID), blankUrls) as LicenceSummaryUiState.Success).licence
        assertNull(licence.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is unknown, then result is NotAvailable with the driver details url`() {
        val result = mapper.toUiModel(makeLicenceDetails(status = LicenceStatus.UNKNOWN), dvlaUrls)
        assertTrue(result is LicenceSummaryUiState.NotAvailable)
        assertEquals(dvlaUrls.driverDetails, (result as LicenceSummaryUiState.NotAvailable).url.originalUrl)
    }

    @Test
    fun `Given licence status is revoked, then result is NotAvailable with the driver details url`() {
        val result = mapper.toUiModel(makeLicenceDetails(status = LicenceStatus.REVOKED), dvlaUrls)
        assertTrue(result is LicenceSummaryUiState.NotAvailable)
        assertEquals(dvlaUrls.driverDetails, (result as LicenceSummaryUiState.NotAvailable).url.originalUrl)
    }

    @Test
    fun `Given licence status is exchanged, then result is NotAvailable with the driver details url`() {
        val result = mapper.toUiModel(makeLicenceDetails(status = LicenceStatus.EXCHANGED), dvlaUrls)
        assertTrue(result is LicenceSummaryUiState.NotAvailable)
        assertEquals(dvlaUrls.driverDetails, (result as LicenceSummaryUiState.NotAvailable).url.originalUrl)
    }

    @Test
    fun `Given licence status is not available and dvlaUrls is null, then NotAvailable falls back to the GOV_UK url`() {
        val result = mapper.toUiModel(makeLicenceDetails(status = LicenceStatus.EXCHANGED), dvlaUrls = null)
        assertTrue(result is LicenceSummaryUiState.NotAvailable)
        assertEquals(GOV_UK_URL, (result as LicenceSummaryUiState.NotAvailable).url.originalUrl)
    }
}
