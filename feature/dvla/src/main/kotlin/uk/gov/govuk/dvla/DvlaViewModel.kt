package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.navigation.ARG_DVLA_TOKEN
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class DvlaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dvlaRepo: DvlaRepo,
    private val analyticsClient: AnalyticsClient,
    @param:Named("dvla_auth_url") private val dvlaAuthUrl: String
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS_INTRO = "DvlaLinkIntroScreen"
        private const val SCREEN_CLASS_SUCCESS = "DvlaLinkSuccessScreen"
        private const val SCREEN_FORMAT = "account bookend"
        private const val SECTION_CONTINUE = "Continue"
        private const val SECTION_LINK_SUCCESS = "account link success"
        private const val NAV_TYPE_CLOSE = "Close"
    }

    sealed interface LinkingEvent {
        data object LinkComplete : LinkingEvent
        data object UnlinkComplete : LinkingEvent
    }

    sealed interface UiState {
        data object Default : UiState
        data object Loading : UiState
        data object Success : UiState
        sealed interface Error : UiState {
            data object Offline : Error
            data object Other : Error
        }
    }

    private val _linkingEvent = MutableSharedFlow<LinkingEvent>()
    val linkingEvent = _linkingEvent.asSharedFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Default)
    val uiState = _uiState.asStateFlow()

    private val _authUrlToLaunch = MutableStateFlow<String?>(null)
    val authUrlToLaunch = _authUrlToLaunch.asStateFlow()

    init {
        val token: String? = savedStateHandle[ARG_DVLA_TOKEN]

        when {
            dvlaRepo.isLinked.value -> unlinkDvlaAccount()
            token != null -> handleAuthRedirect(token)
            else -> startAuthFlow()
        }
    }

    private fun startAuthFlow() {
        _authUrlToLaunch.value = dvlaAuthUrl
    }

    fun onIntroPageView(screenTitle: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS_INTRO,
            screenName = screenTitle,
            title = screenTitle,
            format = SCREEN_FORMAT
        )
    }

    fun onLinkSuccessPageView(screenTitle: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS_SUCCESS,
            screenName = screenTitle,
            title = screenTitle
        )
    }

    fun onIntroCloseClicked() {
        analyticsClient.iconClick(type = NAV_TYPE_CLOSE)
    }

    fun onIntroContinueClicked(text: String) {
        analyticsClient.buttonClick(
            text = text,
            external = false,
            section = SECTION_CONTINUE
        )
    }

    fun onSuccessContinueClicked(text: String) {
        analyticsClient.buttonClick(
            text = text,
            external = false,
            section = SECTION_LINK_SUCCESS
        )

        viewModelScope.launch {
            _linkingEvent.emit(LinkingEvent.LinkComplete)
        }
    }

    fun onAuthTabLaunched() {
        _authUrlToLaunch.value = null
    }

    fun handleAuthRedirect(token: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            linkDvlaAccount(token)
        }
    }

    private suspend fun linkDvlaAccount(token: String, withDelay: Boolean = true) {
        // TODO remove
        if (withDelay) delay(5000)


        when (dvlaRepo.linkAccount(token)) {
            is Result.Success -> _uiState.value = UiState.Success
            is Result.DeviceOffline -> _uiState.value = UiState.Error.Offline
            else -> _uiState.value = UiState.Error.Other
        }
    }

    private fun unlinkDvlaAccount() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (dvlaRepo.unlinkAccount()) {
                is Result.Success -> _linkingEvent.emit(LinkingEvent.UnlinkComplete)
                is Result.DeviceOffline -> _uiState.value = UiState.Error.Offline
                else -> _uiState.value = UiState.Error.Other
            }
        }
    }
}