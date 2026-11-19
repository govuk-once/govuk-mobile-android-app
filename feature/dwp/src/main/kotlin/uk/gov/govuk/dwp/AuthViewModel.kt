package uk.gov.govuk.dwp

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dwp.data.AuthLinkRepo
import uk.gov.govuk.dwp.di.AuthModule.TEMPORARY_LINKING_TOKEN
import uk.gov.govuk.dwp.linking.data.LinkingRepo

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val authLinkRepo: AuthLinkRepo,
    private val linkingRepo: LinkingRepo,
    @param:Named("dwp_auth_url") private val dwpAuthUrl: String
) : ViewModel() {
    sealed interface UiState {
        data object Default : UiState
        sealed interface Loading : UiState {
            data object Auth : Loading
            data object Linking : Loading
        }
        data object Success : UiState
        sealed interface Error : UiState {
            data object Offline : Error
            data object Other : Error
        }
    }

    private val _authUrlToLaunch = MutableStateFlow<String?>(null)
    val authUrlToLaunch = _authUrlToLaunch.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Default)
    val uiState = _uiState.asStateFlow()

    fun onIntroContinueClicked() {
        startAuthFlow()
    }

    fun onRetryClicked() {
        startAuthFlow()
    }

    private fun startAuthFlow() {
        if (_uiState.value != UiState.Default && _uiState.value !is UiState.Error) return
        _authUrlToLaunch.value = null
        _uiState.value = UiState.Loading.Auth
        viewModelScope.launch {
            when (val result = linkingRepo.getVerification()) {
                is Result.Success -> {
                    if (result.value.token.isBlank()) {
                        _uiState.value = UiState.Error.Other
                        return@launch
                    }
                    launchAuthUrl(result.value.token)
                    // TODO: Consume authUrlToLaunch in the route, as DVLA does.
                    handleAuthRedirect(TEMPORARY_LINKING_TOKEN)
                }

                is Result.DeviceOffline -> _uiState.value = UiState.Error.Offline

                else -> _uiState.value = UiState.Error.Other
            }
        }
    }

    private fun launchAuthUrl(
        token: String
    ) {
        _authUrlToLaunch.value = dwpAuthUrl.toUri().buildUpon()
            .appendQueryParameter("token", token)
            .build()
            .toString()
    }

    private fun handleAuthRedirect(
        token: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading.Linking
            linkDwpAccount(token)
        }
    }

    private suspend fun linkDwpAccount(
        token: String
    ) {
        _uiState.value = when (this@AuthViewModel.authLinkRepo.linkAccount(token)) {
            is Result.Success -> UiState.Success
            is Result.DeviceOffline -> UiState.Error.Offline
            else -> UiState.Error.Other
        }
    }
}
