package uk.gov.govuk.dvla

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.domain.DriverSummaryDetails
import uk.gov.govuk.dvla.domain.LicenceDetails

@OptIn(ExperimentalCoroutinesApi::class)
class LicenceSummaryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = mockk<DvlaRepo>(relaxed = true)

    private val licenceDetails = LicenceDetails(
        licenceNumber = "DECER607085K99AE",
        validFrom = "2025-05-02",
        validTo = "2035-05-01",
        type = "Full",
        status = "Valid"
    )

    private val driverSummaryDetails = DriverSummaryDetails(
        licenceNumber = "DECER607085K99AE",
        firstName = "KENNETH",
        lastName = "DECERQUEIRA",
        penaltyPoints = 0,
        status = "Valid",
        expiryDate = "2035-05-01"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given isLinked emits false, when viewModel initialised, then state is Hidden and no calls are made`() = runTest(dispatcher) {
        every { repo.isLinked } returns MutableStateFlow(false)

        val viewModel = LicenceSummaryViewModel(repo)
        advanceUntilIdle()

        assertEquals(LicenceSummaryState.Hidden, viewModel.uiState.value)
        coVerify(exactly = 0) { repo.getLicenceDetails() }
        coVerify(exactly = 0) { repo.getDriverSummary() }
    }

    @Test
    fun `Given isLinked emits true and getLicenceDetails() returns success, when viewModel initialised, then state becomes Success`() = runTest(dispatcher) {
        every { repo.isLinked } returns MutableStateFlow(true)
        coEvery { repo.getLicenceDetails() } returns Result.Success(licenceDetails)

        val viewModel = LicenceSummaryViewModel(repo)

        advanceUntilIdle()

        coVerify(exactly = 1) { repo.getLicenceDetails() }
        coVerify(exactly = 1) { repo.getDriverSummary() }
        assertEquals(LicenceSummaryState.Success(licenceDetails), viewModel.uiState.value)
    }

    @Test
    fun `Given isLinked emits true and getLicenceDetails() returns error, when viewModel initialised, then state becomes Error`() = runTest(dispatcher) {
        every { repo.isLinked } returns MutableStateFlow(true)
        coEvery { repo.getLicenceDetails() } returns Result.Error()

        val viewModel = LicenceSummaryViewModel(repo)
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.getLicenceDetails() }
        coVerify(exactly = 1) { repo.getDriverSummary() }
        assertEquals(LicenceSummaryState.Error, viewModel.uiState.value)
    }

    @Test
    fun `Given account is linked then unlinked, state switches from Success to Hidden`() = runTest(dispatcher) {
        val isLinkedFlow = MutableStateFlow(true)
        every { repo.isLinked } returns isLinkedFlow
        coEvery { repo.getLicenceDetails() } returns Result.Success(licenceDetails)

        val viewModel = LicenceSummaryViewModel(repo)
        advanceUntilIdle()

        assertEquals(LicenceSummaryState.Success(licenceDetails), viewModel.uiState.value)

        // unlinking
        isLinkedFlow.value = false
        advanceUntilIdle()

        assertEquals(LicenceSummaryState.Hidden, viewModel.uiState.value)
    }
}
