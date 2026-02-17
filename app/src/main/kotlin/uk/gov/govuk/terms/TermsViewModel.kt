package uk.gov.govuk.terms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.terms.data.TermsRepo
import javax.inject.Inject

internal data class TermsUiState(val termsUrl: String)

@HiltViewModel
internal class TermsViewModel @Inject constructor(
    configRepo: ConfigRepo,
    private val termsRepo: TermsRepo
) : ViewModel() {

    private val _uiState: MutableStateFlow<TermsUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _termsAccepted = MutableSharedFlow<Unit>()
    val termsAccepted: SharedFlow<Unit> = _termsAccepted

    init {
        _uiState.value = TermsUiState(configRepo.termsAndConditions?.url ?: "")
    }

    internal fun onTermsAccepted() {
        viewModelScope.launch {
            termsRepo.termsAccepted()
            _termsAccepted.emit(Unit)
        }
    }
}
