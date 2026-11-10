package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.mapper.LicenceCategoriesMapper
import uk.gov.govuk.dvla.ui.model.LicenceCategoriesUiModel
import javax.inject.Inject

internal sealed interface LicenceCategoriesUiState {
    data class Success(val categories: LicenceCategoriesUiModel) : LicenceCategoriesUiState
    data object Error : LicenceCategoriesUiState
}

@HiltViewModel
internal class LicenceCategoriesViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val categoriesMapper: LicenceCategoriesMapper,
    private val dvlaRepo: DvlaRepo,
    private val configRepo: ConfigRepo
) : ViewModel() {

    private companion object {
        const val SCREEN_CLASS = "LicenceCategoriesScreen"
        const val CATEGORY_INFO_TEXT = "Vehicle Category"
        const val SECTION = "Driving"
    }

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState = _uiState.asStateFlow()

    private fun createInitialState(): LicenceCategoriesUiState {
        val licence = dvlaRepo.getCachedLicenceDetails()
            ?: return LicenceCategoriesUiState.Error

        val categories = categoriesMapper.toUiModel(licence.entitlements, configRepo.dvlaUrls)
        return LicenceCategoriesUiState.Success(categories)
    }

    fun onPageView(title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )
    }

    fun onCategoryInfoClicked() {
        analyticsClient.buttonClick(
            text = CATEGORY_INFO_TEXT,
            section = SECTION
        )
    }

    fun onFurtherInfoLinkClicked(text: String, url: String) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = SECTION
        )
    }
}
