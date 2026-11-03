package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.EntitlementType
import uk.gov.govuk.dvla.mapper.LicenceCategoriesMapper
import uk.gov.govuk.dvla.navigation.ARG_CATEGORY_CODE
import uk.gov.govuk.dvla.navigation.ARG_ENTITLEMENT_TYPE
import uk.gov.govuk.dvla.ui.model.LicenceCategoryDetailUiModel
import javax.inject.Inject

internal sealed interface LicenceCategoryDetailUiState {
    data class Success(val detail: LicenceCategoryDetailUiModel) : LicenceCategoryDetailUiState
    data object Error : LicenceCategoryDetailUiState
}

@HiltViewModel
internal class LicenceCategoryDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val analyticsClient: AnalyticsClient,
    private val categoriesMapper: LicenceCategoriesMapper,
    private val dvlaRepo: DvlaRepo
) : ViewModel() {

    private companion object {
        const val SCREEN_CLASS = "LicenceCategoryDetailScreen"
    }

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState = _uiState.asStateFlow()

    private fun createInitialState(): LicenceCategoryDetailUiState {
        val code: String? = savedStateHandle[ARG_CATEGORY_CODE]
        val typeName: String? = savedStateHandle[ARG_ENTITLEMENT_TYPE]
        val type = EntitlementType.entries.firstOrNull { it.name == typeName }

        val licence = dvlaRepo.getCachedLicenceDetails()
            ?: return LicenceCategoryDetailUiState.Error

        // code is the display code (eg 'f')
        val entitlement = licence.entitlements.firstOrNull {
            it.categoryCode.equals(code, ignoreCase = true) && it.categoryType == type
        } ?: return LicenceCategoryDetailUiState.Error

        val categoryDetail = categoriesMapper.toDetailUiModel(entitlement)
            ?: return LicenceCategoryDetailUiState.Error

        return LicenceCategoryDetailUiState.Success(categoryDetail)
    }

    fun onPageView(title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )
    }
}