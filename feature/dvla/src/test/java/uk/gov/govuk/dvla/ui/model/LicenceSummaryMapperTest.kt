package uk.gov.govuk.dvla.ui.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.dvla.domain.DriverSummary
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

    private fun makeDriverSummary(
        status: LicenceStatus,
        expiryDate: LocalDate? = LocalDate.now().plusYears(1)
    ) = DriverSummary(
        licenceType = LicenceType.FULL,
        licenceNumber = "ARENO803236AA170",
        title = "Ms",
        firstNames = "Anna Ornella",
        lastName = "Areno",
        addressLine1 = "29 Orchard Drive",
        addressLine5 = "Milton Keynes",
        postcode = "PA98 J83",
        status = status,
        expiryDate = expiryDate
    )

    @Test
    fun `Given licence status is valid, then drivingRecordUrl is populated from dvlaUrls`() {
        val result = mapper.toUiModel(makeDriverSummary(status = LicenceStatus.VALID), dvlaUrls)
        assertEquals(dvlaUrls.drivingRecord, result.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is valid and expiring soon, then drivingRecordUrl is still populated`() {
        val result = mapper.toUiModel(
            makeDriverSummary(status = LicenceStatus.VALID, expiryDate = LocalDate.now().plusDays(10)),
            dvlaUrls
        )
        assertEquals(dvlaUrls.drivingRecord, result.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is expired, then drivingRecordUrl is null`() {
        val result = mapper.toUiModel(makeDriverSummary(status = LicenceStatus.EXPIRED), dvlaUrls)
        assertNull(result.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is unknown, then drivingRecordUrl is null`() {
        val result = mapper.toUiModel(makeDriverSummary(status = LicenceStatus.UNKNOWN), dvlaUrls)
        assertNull(result.drivingRecordUrl)
    }

    @Test
    fun `Given licence status is revoked, then drivingRecordUrl is null`() {
        val result = mapper.toUiModel(makeDriverSummary(status = LicenceStatus.REVOKED), dvlaUrls)
        assertNull(result.drivingRecordUrl)
    }

    @Test
    fun `Given dvlaUrls is null, then drivingRecordUrl is null even when licence is valid`() {
        val result = mapper.toUiModel(makeDriverSummary(status = LicenceStatus.VALID), dvlaUrls = null)
        assertNull(result.drivingRecordUrl)
    }

    @Test
    fun `Given dvlaUrls drivingRecord is blank, then drivingRecordUrl is null even when licence is valid`() {
        val blankUrls = dvlaUrls.copy(drivingRecord = "")
        val result = mapper.toUiModel(makeDriverSummary(status = LicenceStatus.VALID), blankUrls)
        assertNull(result.drivingRecordUrl)
    }
}
