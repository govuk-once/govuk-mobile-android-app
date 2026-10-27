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
    // TODO GOVUKAPP-4152 do we need to cover empty state?
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

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState = _uiState.asStateFlow()

    // TODO GOVUKAPP-4152 add urls to remote config
    val dvlaUrls = configRepo.dvlaUrls
    private fun createInitialState(): LicenceCategoriesUiState {
        val licence = dvlaRepo.getCachedLicenceDetails()
            ?: return LicenceCategoriesUiState.Error

        val categories = categoriesMapper.toUiModel(licence.entitlements)
        return LicenceCategoriesUiState.Success(categories)
    }

//    screen_view on page load: Screen title and name should both be the “title of the screen”
    fun onPageView() {

    }

//    Navigation event on “Info “with following parameters
//    text: Vehicle Category
//    type: Button
//    external: false
//    section: Driving
    fun onCategoryInfoClicked() {

    }



//    type: button
//    text: “Link name”
//    external: True
//    section: Driving
//    URL: “The URL for each option”
    fun onFurtherInfoLinkClicked(text: String, url: String) {

    }

}