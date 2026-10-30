package uk.gov.govuk.dvla

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.Entitlement
import uk.gov.govuk.dvla.domain.EntitlementType
import uk.gov.govuk.dvla.domain.LicenceDetails
import uk.gov.govuk.dvla.mapper.LicenceCategoriesMapper
import uk.gov.govuk.dvla.ui.model.LicenceCategoriesUiModel

class LicenceCategoriesViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val categoriesMapper = mockk<LicenceCategoriesMapper>()
    private val dvlaRepo = mockk<DvlaRepo>()
    private val configRepo = mockk<ConfigRepo>()
    private val dvlaUrls = mockk<DvlaUrls>()
    private val entitlements = listOf(Entitlement("B", EntitlementType.FULL))
    private val licence = mockk<LicenceDetails>()
    private val categories = mockk<LicenceCategoriesUiModel>()
    private lateinit var viewModel: LicenceCategoriesViewModel

    @Before
    fun setup() {
        every { licence.entitlements } returns entitlements
        every { dvlaRepo.getCachedLicenceDetails() } returns licence
        every { configRepo.dvlaUrls } returns dvlaUrls
        every { categoriesMapper.toUiModel(entitlements, dvlaUrls) } returns categories

        viewModel = LicenceCategoriesViewModel(
            analyticsClient, categoriesMapper, dvlaRepo, configRepo
        )
    }

    @Test
    fun `Given a cached licence, when viewModel is created, then state is Success with the mapped categories`() {
        assertEquals(LicenceCategoriesUiState.Success(categories), viewModel.uiState.value)
    }

    @Test
    fun `Given a cached licence and no dvla urls, when viewModel created, then state is Success`() {
        every { configRepo.dvlaUrls } returns null
        every { categoriesMapper.toUiModel(entitlements, null) } returns categories

        viewModel = LicenceCategoriesViewModel(
            analyticsClient, categoriesMapper, dvlaRepo, configRepo
        )

        assertEquals(LicenceCategoriesUiState.Success(categories), viewModel.uiState.value)
    }

    @Test
    fun `When onPageView called, then screen view is tracked with the title`() {
        viewModel.onPageView("Categories")

        verify(exactly = 1) {
            analyticsClient.screenView(
                screenClass = "LicenceCategoriesScreen",
                screenName = "Categories",
                title = "Categories"
            )
        }
    }

    @Test
    fun `When onCategoryInfoClicked called, then button click is tracked`() {
        viewModel.onCategoryInfoClicked()

        verify(exactly = 1) {
            analyticsClient.buttonClick(text = "Vehicle Category", section = "Driving")
        }
    }

    @Test
    fun `When onFurtherInfoLinkClicked called, then external button click is tracked`() {
        viewModel.onFurtherInfoLinkClicked(
            text = "Vehicle weights explained",
            url = "https://www.gov.uk/vehicle-weights-explained"
        )

        verify(exactly = 1) {
            analyticsClient.buttonClick(
                text = "Vehicle weights explained",
                url = "https://www.gov.uk/vehicle-weights-explained",
                external = true,
                section = "Driving"
            )
        }
    }
}