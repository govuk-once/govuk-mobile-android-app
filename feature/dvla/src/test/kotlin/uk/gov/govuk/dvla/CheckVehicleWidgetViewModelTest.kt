package uk.gov.govuk.dvla

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.data.model.Result

@OptIn(ExperimentalCoroutinesApi::class)
class CheckVehicleWidgetViewModelTest {

    private val dvlaRepo = mockk<DvlaRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    private lateinit var viewModel: CheckVehicleWidgetViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = CheckVehicleWidgetViewModel(dvlaRepo, analyticsClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a search click, when onSearchClicked is called, then track button click event`() {
        val expectedText = "Check vehicle details"

        viewModel.onSearchVehicleClicked(expectedText)

        verify(exactly = 1) {
            analyticsClient.buttonClick(
                text = expectedText,
                section = "Driving"
            )
        }
    }
    
    @Test
    fun `When input contains characters other than alphanumeric, then they are removed`() {
        viewModel.onRegistrationNumberChanged("AB12@£")
        
        assertEquals(CheckVehicleSheetState.Input("AB12"), viewModel.sheetState.value)
    }

    @Test
    fun `When input exceeds 7 characters, then only 7 are accepted`() {
        viewModel.onRegistrationNumberChanged("AB12CDE")
        viewModel.onRegistrationNumberChanged("AB12CDEF")
        
        assertEquals(CheckVehicleSheetState.Input("AB12CDE"), viewModel.sheetState.value)
    }
    
    @Test
    fun `When onClearClicked, then state is empty Input`() {
        viewModel.onRegistrationNumberChanged("AB12CDE")
        
        viewModel.onClearClicked()
        
        assertEquals(CheckVehicleSheetState.Input(), viewModel.sheetState.value)
    }
    
    @Test
    fun `When onSubmitClicked, then search event is tracked`() = runTest(dispatcher) {
        viewModel.onRegistrationNumberChanged("AB12CDE")
        viewModel.onSubmitClicked("Submit")
        
        verify(exactly = 1) {
            analyticsClient.widgetFunction(
                text = "Submit",
                section = "Driving",
                action = "Search number plate"
            )
        }
    }
    
    @Test
    fun `When onSubmitClicked, then state is Loading with the registration number entered`() = runTest(dispatcher) {
        viewModel.onRegistrationNumberChanged("AB12 CDE")
        viewModel.onSubmitClicked("Submit")
        
        assertEquals(CheckVehicleSheetState.Loading("AB12 CDE"), viewModel.sheetState.value)
    }
    
    @Test
    fun `Given state is loading, when onSubmitClicked called again, then only one lookup is made`() = runTest(dispatcher) {
        viewModel.onRegistrationNumberChanged("AB12CDE")
        
        viewModel.onSubmitClicked("Submit")
        viewModel.onSubmitClicked("Submit")
        advanceUntilIdle()
        
        coVerify(exactly = 1) { dvlaRepo.lookupVehicleByRegistration(any()) }
        verify(exactly = 1) {
            analyticsClient.widgetFunction(text = any(), section = any(), action = any())
        }
    }

    @Test
    fun `Given lookup returns 404, then state is NUMBER_PLATE_NOT_FOUND error`() = runTest(dispatcher) {
        coEvery { dvlaRepo.lookupVehicleByRegistration(any()) } returns Result.ServiceNotResponding(404)
        viewModel.onRegistrationNumberChanged("AB12 CDE")
        
        viewModel.onSubmitClicked("Submit")
        advanceUntilIdle()
        
        assertEquals(
            CheckVehicleSheetState.Error("AB12 CDE", CheckVehicleError.NUMBER_PLATE_NOT_FOUND),
            viewModel.sheetState.value
        )
    }
    
    @Test
    fun `Given lookup returns HTTP error other than 404, then state is SEARCH_UNAVAILABLE error`() = runTest(dispatcher) {
        coEvery { dvlaRepo.lookupVehicleByRegistration(any()) } returns Result.ServiceNotResponding(500)
        viewModel.onRegistrationNumberChanged("AB12CDE")
        
        viewModel.onSubmitClicked("Submit")
        advanceUntilIdle()
        
        assertEquals(
            CheckVehicleSheetState.Error("AB12CDE", CheckVehicleError.SEARCH_UNAVAILABLE),
            viewModel.sheetState.value
        )
    }
    
    @Test
    fun `Given lookup returns generic Error, then state is SEARCH_UNAVAILABLE error`() = runTest(dispatcher) {
        coEvery { dvlaRepo.lookupVehicleByRegistration(any()) } returns Result.Error()
        viewModel.onRegistrationNumberChanged("AB12CDE")
        
        viewModel.onSubmitClicked("Submit")
        advanceUntilIdle()
        
        assertEquals(
            CheckVehicleSheetState.Error("AB12CDE", CheckVehicleError.SEARCH_UNAVAILABLE),
            viewModel.sheetState.value
        )
    }
    
    @Test
    fun `When onCancelClicked is called, then cancel event is tracked and state is reset`() {
        viewModel.onRegistrationNumberChanged("AB12CDE")
        viewModel.onCancelClicked("Cancel")
        
        verify(exactly = 1) {
            analyticsClient.widgetFunction(
                text = "Cancel",
                section = "Driving",
                action = "Cancel search number plate"
            )
        }

        assertEquals(CheckVehicleSheetState.Input(), viewModel.sheetState.value)
    }
    
    @Test
    fun `When onSheetDismissed is called, then state is reset and nothing is tracked`() {
        viewModel.onRegistrationNumberChanged("AB12CDE")
        
        viewModel.onSheetDismissed()
        
        assertEquals(CheckVehicleSheetState.Input(), viewModel.sheetState.value)
        verify(exactly = 0) {
            analyticsClient.widgetFunction(text = any(), section = any(), action = any())
        }
    }

    @Test
    fun `Given lookup in progress, when onCancelClicked, then result does not change state`() = runTest(dispatcher) {
        coEvery { dvlaRepo.lookupVehicleByRegistration(any()) } returns Result.Success(mockk())

        viewModel.onRegistrationNumberChanged("AB12CDE")
        viewModel.onSubmitClicked("Submit")
        viewModel.onCancelClicked("Cancel")
        advanceUntilIdle()

        assertEquals(CheckVehicleSheetState.Input(), viewModel.sheetState.value)
    }
}