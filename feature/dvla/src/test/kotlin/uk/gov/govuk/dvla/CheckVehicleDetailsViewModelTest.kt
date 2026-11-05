package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.VehicleEnquiryDetails
import uk.gov.govuk.dvla.mapper.CheckVehicleDetailsMapper
import uk.gov.govuk.dvla.navigation.ARG_REG_NUMBER
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

class CheckVehicleDetailsViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val mapper = mockk<CheckVehicleDetailsMapper>()
    private val dvlaRepo = mockk<DvlaRepo>()
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val regNumber = "AB12CDE"
    private val savedStateHandle = SavedStateHandle(mapOf(ARG_REG_NUMBER to regNumber))
    private val vehicleDetails = mockk<VehicleDetailsUiModel>()
    private lateinit var viewModel: CheckVehicleDetailsViewModel

    @Before
    fun setup() {
        val enquiry = mockk<VehicleEnquiryDetails>()

        every { dvlaRepo.getCachedVehicleEnquiryDetails(regNumber) } returns enquiry
        every { mapper.toUiModel(enquiry, any()) } returns vehicleDetails

        viewModel = CheckVehicleDetailsViewModel(
            analyticsClient, mapper, savedStateHandle, dvlaRepo, configRepo
        )
    }

    @Test
    fun `Given a cached vehicle result, when viewModel created, then state is Success`() {
        assertEquals(CheckVehicleDetailsUiState.Success(vehicleDetails), viewModel.uiState.value)
    }

    @Test
    fun `Given no cached vehicle result, when viewModel created, then state is Error with the registration number`() {
        every { dvlaRepo.getCachedVehicleEnquiryDetails(regNumber) } returns null

        viewModel = CheckVehicleDetailsViewModel(
            analyticsClient, mapper, savedStateHandle, dvlaRepo, configRepo
        )

        assertEquals(CheckVehicleDetailsUiState.Error(regNumber), viewModel.uiState.value)
    }

    @Test
    fun `Given no registration nav argument, when viewModel created, then state is Error with empty registration`() {
        every { dvlaRepo.getCachedVehicleEnquiryDetails("") } returns null

        viewModel = CheckVehicleDetailsViewModel(
            analyticsClient, mapper, SavedStateHandle(), dvlaRepo, configRepo
        )

        assertEquals(CheckVehicleDetailsUiState.Error(""), viewModel.uiState.value)
    }

    @Test
    fun `When onPageView called, then screen view is tracked`() {
        viewModel.onPageView()

        verify(exactly = 1) {
            analyticsClient.screenView(
                screenClass = "VehicleDetailsScreen",
                screenName = "VehicleDetailsScreenSearchResult",
                title = "VehicleDetailsScreenSearchResult"
            )
        }
    }

    @Test
    fun `When onCloseClicked called, then button click is tracked`() {
        viewModel.onCloseClicked()

        verify(exactly = 1) {
            analyticsClient.buttonClick(text = "Back", section = "Driving")
        }
    }

    @Test
    fun `When onSearchClicked called, then button click is tracked`() {
        viewModel.onSearchClicked()

        verify(exactly = 1) {
            analyticsClient.buttonClick(text = "Search", section = "Driving")
        }
    }

    @Test
    fun `When menu item is clicked , then menu item click is tracked`() {
        viewModel.onMenuItemClicked(text = "Used vehicle checks", url = "https://www.gov.uk")

        verify(exactly = 1) {
            analyticsClient.menuItemClick(
                text = "Used vehicle checks",
                url = "https://www.gov.uk",
                external = true,
                section = "Driving"
            )
        }
    }
}