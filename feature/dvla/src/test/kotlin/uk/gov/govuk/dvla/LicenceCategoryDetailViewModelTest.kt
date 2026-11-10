package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.Entitlement
import uk.gov.govuk.dvla.domain.EntitlementType
import uk.gov.govuk.dvla.domain.LicenceDetails
import uk.gov.govuk.dvla.mapper.LicenceCategoriesMapper
import uk.gov.govuk.dvla.navigation.ARG_CATEGORY_CODE
import uk.gov.govuk.dvla.navigation.ARG_ENTITLEMENT_TYPE
import uk.gov.govuk.dvla.ui.model.LicenceCategoryDetailUiModel

class LicenceCategoryDetailViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val categoriesMapper = mockk<LicenceCategoriesMapper>()
    private val dvlaRepo = mockk<DvlaRepo>()
    private val catAFull = Entitlement("A", EntitlementType.FULL)
    private val catAProvisional = Entitlement("A", EntitlementType.PROVISIONAL)
    private val catFFull = Entitlement("F", EntitlementType.FULL)
    private val licence = mockk<LicenceDetails>()
    private val licenceCategoryDetail = mockk<LicenceCategoryDetailUiModel>()
    private lateinit var viewModel: LicenceCategoryDetailViewModel

    @Before
    fun setup() {
        every { licence.entitlements } returns listOf(catAFull, catAProvisional, catFFull)
        every { dvlaRepo.getCachedLicenceDetails() } returns licence
        every { categoriesMapper.toDetailUiModel(any()) } returns licenceCategoryDetail

        viewModel = createViewModel()
    }

    private fun createViewModel(code: String? = "A", type: String? = "FULL") =
        LicenceCategoryDetailViewModel(
            SavedStateHandle(mapOf(ARG_CATEGORY_CODE to code, ARG_ENTITLEMENT_TYPE to type)),
            analyticsClient,
            categoriesMapper,
            dvlaRepo
        )

    @Test
    fun `Given a cached licence with entitlement, when viewModel is created, then state is Success with the mapped licence category detail`() {
        assertEquals(LicenceCategoryDetailUiState.Success(licenceCategoryDetail), viewModel.uiState.value)
        verify(exactly = 1) { categoriesMapper.toDetailUiModel(catAFull) }
    }

    @Test
    fun `Given a code held as full and provisional, when viewModel is created with provisional type, then the provisional entitlement is mapped`() {
        viewModel = createViewModel(type = "PROVISIONAL")

        verify(exactly = 1) { categoriesMapper.toDetailUiModel(catAProvisional) }
    }

    @Test
    fun `Given no matching entitlement, when viewModel is created, then state is Error`() {
        viewModel = createViewModel(code = "B")

        assertEquals(LicenceCategoryDetailUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `Given the mapper returns null, when viewModel is created, then state is Error`() {
        every { categoriesMapper.toDetailUiModel(any()) } returns null

        viewModel = createViewModel()

        assertEquals(LicenceCategoryDetailUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `Given no cached licence, when viewModel is created, then state is Error`() {
        every { dvlaRepo.getCachedLicenceDetails() } returns null

        viewModel = createViewModel()

        assertEquals(LicenceCategoryDetailUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `Given no category code, when viewModel is created, then state is Error`() {
        viewModel = createViewModel(code = null)

        assertEquals(LicenceCategoryDetailUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `Given an unrecognised entitlement type, when viewModel is created, then state is Error`() {
        viewModel = createViewModel(type = "INVALID_TYPE")

        assertEquals(LicenceCategoryDetailUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `When onPageView called, then screen view is tracked with the title`() {
        viewModel.onPageView("Minibuses")

        verify(exactly = 1) {
            analyticsClient.screenView(
                screenClass = "LicenceCategoryDetailScreen",
                screenName = "Minibuses",
                title = "Minibuses"
            )
        }
    }

    @Test
    fun `When onBackClicked called, then button click is tracked`() {
        viewModel.onBackClicked()

        verify(exactly = 1) {
            analyticsClient.buttonClick(text = "Back", section = "Driving")
        }
    }
}