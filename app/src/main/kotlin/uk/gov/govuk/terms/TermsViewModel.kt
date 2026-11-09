package uk.gov.govuk.terms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.BuildConfig
import uk.gov.govuk.terms.data.TermsAcceptanceState
import uk.gov.govuk.terms.data.TermsRepo
import javax.inject.Inject

internal sealed class TermsUiState {

    data class Terms(
        val termsUrl: String,
        val privacyPolicyUrl: String,
        val isUpdated: Boolean
    ): TermsUiState()

    data object Error: TermsUiState()
}

@HiltViewModel
internal class TermsViewModel @Inject constructor(
    private val termsRepo: TermsRepo
) : ViewModel() {

    private val _uiState: MutableStateFlow<TermsUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _termsAccepted = MutableSharedFlow<Unit>()
    val termsAccepted: SharedFlow<Unit> = _termsAccepted

    init {
        viewModelScope.launch {
            val state = termsRepo.getTermsAcceptanceState()

            if (state is TermsAcceptanceState.Accepted) {
                // Should never happen
                _termsAccepted.emit(Unit)
            } else {
                _uiState.value = when (state) {
                    is TermsAcceptanceState.NewUser ->
                        TermsUiState.Terms(
                            termsUrl = state.termsUrl,
                            privacyPolicyUrl = BuildConfig.PRIVACY_POLICY_URL,
                            isUpdated = false
                        )

                    is TermsAcceptanceState.Updated ->
                        TermsUiState.Terms(
                            termsUrl = state.termsUrl,
                            privacyPolicyUrl = BuildConfig.PRIVACY_POLICY_URL,
                            isUpdated = true
                        )

                    else -> TermsUiState.Error
                }
            }
        }
    }

    internal fun onTermsAccepted() {
        viewModelScope.launch {
            termsRepo.termsAccepted()
            _termsAccepted.emit(Unit)
        }
    }
}
