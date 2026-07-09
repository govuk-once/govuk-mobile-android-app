package uk.gov.govuk.dvla.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.identity.IdentityRepo
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.data.local.DvlaDataStore
import uk.gov.govuk.dvla.remote.DvlaApi
import uk.gov.govuk.dvla.remote.model.CustomerVehicleDetailsResponse
import uk.gov.govuk.dvla.remote.model.CustomerVehiclesResponse
import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse
import uk.gov.govuk.dvla.remote.model.LicenceResponse
import uk.gov.govuk.dvla.remote.model.MultiShareCodeResponse
import uk.gov.govuk.dvla.remote.model.SingleShareCodeResponse
import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse

class DvlaRepoTest {

    private val api = mockk<DvlaApi>()
    private val authRepo = mockk<AuthRepo>()
    private val dvlaDataStore = mockk<DvlaDataStore>()
    private val identityRepo = mockk<IdentityRepo>()
    private lateinit var repo: DvlaRepo
    private val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJleHAiOjM2MDAsImxpbmtpbmdfaWQiOiIxMjM0LWFiY2QifQ."

    @Before
    fun setup() {
        every { identityRepo.linkStatusOf(LinkedService.DVLA) } returns flowOf(ServiceLinkStatus.UNLINKED)
        every { identityRepo.currentStatusOf(LinkedService.DVLA) } returns ServiceLinkStatus.UNLINKED

        repo = DvlaRepo(api, authRepo, dvlaDataStore, identityRepo)
    }

    @Test
    fun `Given getSelectedDrivingView is called, then getSelectedDrivingView is called on the data store`() =
        runTest {
            coEvery { dvlaDataStore.getSelectedDrivingView() } returns DrivingView.VEHICLES

            repo.getSelectedDrivingView()

            coVerify(exactly = 1) { dvlaDataStore.getSelectedDrivingView() }
        }

    @Test
    fun `Given setSelectedDrivingView is called, then setSelectedDrivingView is called on the data store`() =
        runTest {
            coEvery { dvlaDataStore.setSelectedDrivingView(drivingView = DrivingView.VEHICLES) } returns Unit

            repo.setSelectedDrivingView(drivingView = DrivingView.VEHICLES)

            coVerify(exactly = 1) { dvlaDataStore.setSelectedDrivingView(drivingView = DrivingView.VEHICLES) }
        }

    @Test
    fun `Given clear is called, then clear is called on the data store`() =
        runTest {
            coEvery { dvlaDataStore.clear() } returns Unit

            repo.clear()

            coVerify(exactly = 1) {
                dvlaDataStore.clear()
            }
        }

    @Test
    fun `Given linking api returns success, when linkAccount is called, then return Success and sync Identity`() = runTest {
        coEvery { api.linkDvlaIdentity(token) } returns Response.success(Unit)
        coEvery { identityRepo.getLinkedServices() } returns Unit

        val result = repo.linkAccount(token)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.linkDvlaIdentity(token) }
        coVerify(exactly = 1) { identityRepo.getLinkedServices() }
    }

    @Test
    fun `Given linking api throws exception, when linkAccount is called, then return Error`() = runTest {
        coEvery { api.linkDvlaIdentity(token) } throws Exception("Exception")

        val result = repo.linkAccount(token)

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.linkDvlaIdentity(token) }
    }

    @Test
    fun `Given unlinking api returns success, when unlinkAccount is called, then return Success and and sync identity`() = runTest {
        coEvery { api.deleteDvlaIdentity() } returns Response.success(Unit)
        coEvery { dvlaDataStore.clear() } returns Unit
        coEvery { identityRepo.getLinkedServices() } returns Unit

        val result = repo.unlinkAccount()

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.deleteDvlaIdentity() }
        coVerify(exactly = 1) { dvlaDataStore.clear() }
        coVerify(exactly = 1) { identityRepo.getLinkedServices() }
    }

    @Test
    fun `Given unlinking api throws exception, when unlinkAccount is called, then return error`() = runTest {
        coEvery { api.deleteDvlaIdentity() } throws Exception("Exception")

        val result = repo.unlinkAccount()

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.deleteDvlaIdentity() }
        coVerify(exactly = 0) { dvlaDataStore.clear() }
        coVerify(exactly = 0) { identityRepo.getLinkedServices() }
    }

    @Test
    fun `Given refreshLinkStatus is called, then call getLinkedServices on identityRepo`() = runTest {
        coEvery { identityRepo.getLinkedServices() } returns Unit

        repo.refreshLinkStatus()

        coVerify(exactly = 1) { identityRepo.getLinkedServices() }
    }

    @Test
    fun `Given driving licence api returns success, when getLicenceDetails is called, then return Success with LicenceDetails`() = runTest {
        val licenceResponse = mockk<LicenceResponse>(relaxed = true)

        coEvery { api.getDrivingLicence() } returns Response.success(licenceResponse)

        val result = repo.getLicenceDetails()

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.getDrivingLicence() }
    }

    @Test
    fun `Given driving licence api fails, when getLicenceDetails is called, then return Error`() = runTest {
        coEvery { api.getDrivingLicence() } throws Exception("Exception")

        val result = repo.getLicenceDetails()

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.getDrivingLicence() }
    }

    @Test
    fun `Given driver summary api returns success, when getDriverSummary is called, then return Success with DriverSummaryDetails`() = runTest {
        val summaryResponse = mockk<DriverSummaryResponse>(relaxed = true)
        coEvery { api.getDriverSummary() } returns Response.success(summaryResponse)

        val result = repo.getDriverSummary()

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.getDriverSummary() }
    }

    @Test
    fun `Given driver summary api fails, when getDriverSummary is called, then return Error`() = runTest {
        coEvery { api.getDriverSummary() } throws Exception("Exception")

        val result = repo.getDriverSummary()

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.getDriverSummary() }
    }

    @Test
    fun `Given customer vehicles api returns success, when getCustomerVehicles is called, then return Success with vehicles`() = runTest {
        val vehiclesResponse = mockk<CustomerVehiclesResponse>(relaxed = true) {
            every { customerVehicles } returns emptyList()
        }
        coEvery { api.getCustomerVehicles() } returns Response.success(vehiclesResponse)

        val result = repo.getCustomerVehicles()

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.getCustomerVehicles() }
    }

    @Test
    fun `Given customer vehicles api fails, when getCustomerVehicles is called, then return Error`() = runTest {
        coEvery { api.getCustomerVehicles() } throws Exception("Exception")

        val result = repo.getCustomerVehicles()

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.getCustomerVehicles() }
    }

    @Test
    fun `Given vehicle details api returns success, when getVehicleDetails is called, then return Success with VehicleDetails`() = runTest {
        val vehicleId = 156487251
        val detailsResponse = mockk<CustomerVehicleDetailsResponse>(relaxed = true)
        coEvery { api.getVehicleDetails(vehicleId) } returns Response.success(detailsResponse)

        val result = repo.getVehicleDetails(vehicleId)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.getVehicleDetails(vehicleId) }
    }

    @Test
    fun `Given vehicle details api fails, when getVehicleDetails is called, then return Error`() = runTest {
        val vehicleId = 156487251
        coEvery { api.getVehicleDetails(vehicleId) } throws Exception("Exception")

        val result = repo.getVehicleDetails(vehicleId)

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.getVehicleDetails(vehicleId) }
    }

    @Test
    fun `Given vehicle enquiry returns success, when getVehicleDetails is called, then return Success with VehicleDetails`() = runTest {
        val reg = "AA19AAA"
        val vehicleResponse = mockk<VehicleEnquiryResponse>(relaxed = true)

        coEvery { api.lookupVehicle(reg) } returns Response.success(vehicleResponse)

        val result = repo.lookupVehicle(reg)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.lookupVehicle(reg) }
    }

    @Test
    fun `Given vehicle enquiry fails, when getVehicleDetails is called, then return Error`() = runTest {
        val reg = "AA19AAA"

        coEvery { api.lookupVehicle(reg) } throws Exception("Exception")

        val result = repo.lookupVehicle(reg)

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.lookupVehicle(reg) }
    }

    @Test
    fun `Given create share code api returns success, when createShareCode is called, then return Success with ShareCodeDetails`() = runTest {
        val shareCodeResponse = mockk<SingleShareCodeResponse>(relaxed = true)

        coEvery { api.createShareCode() } returns Response.success(shareCodeResponse)

        val result = repo.createCheckCode()

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.createShareCode() }
    }

    @Test
    fun `Given create share code api fails, when createShareCode is called, then return Error`() = runTest {
        coEvery { api.createShareCode() } throws Exception("Exception")

        val result = repo.createCheckCode()

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.createShareCode() }
    }

    @Test
    fun `Given get share codes api returns success, when getShareCodes is called, then return Success with list of ShareCodeDetails`() = runTest {
        val shareCodesResponse = mockk<MultiShareCodeResponse>(relaxed = true)

        coEvery { api.getShareCodes() } returns Response.success(shareCodesResponse)

        val result = repo.getCheckCodes()

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.getShareCodes() }
    }

    @Test
    fun `Given get share codes api fails, when getShareCodes is called, then return Error`() = runTest {
        coEvery { api.getShareCodes() } throws Exception("Exception")

        val result = repo.getCheckCodes()

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.getShareCodes() }
    }

    @Test
    fun `Given cancel share code api returns success, when cancelShareCode is called, then return Success with ShareCodeDetails`() = runTest {
        val tokenId = "token_id"
        val shareCodeResponse = mockk<SingleShareCodeResponse>(relaxed = true)

        coEvery { api.cancelShareCode(tokenId) } returns Response.success(shareCodeResponse)

        val result = repo.cancelCheckCode(tokenId)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { api.cancelShareCode(tokenId) }
    }

    @Test
    fun `Given cancel share code api fails, when cancelShareCode is called, then return Error`() = runTest {
        val tokenId = "token_id"
        coEvery { api.cancelShareCode(tokenId) } throws Exception("Exception")

        val result = repo.cancelCheckCode(tokenId)

        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { api.cancelShareCode(tokenId) }
    }
}
