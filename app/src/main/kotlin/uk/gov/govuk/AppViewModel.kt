package uk.gov.govuk

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.ChatFeature
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.login.data.LoginRepo
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE
import uk.gov.govuk.search.SearchFeature
import uk.gov.govuk.terms.data.TermsAcceptanceState
import uk.gov.govuk.terms.data.TermsRepo
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.visited.Visited
import uk.gov.govuk.widgets.model.HomeWidget
import uk.govuk.app.local.LocalFeature
import javax.inject.Inject

@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val timeoutManager: TimeoutManager,
    private val appRepo: AppRepo,
    private val loginRepo: LoginRepo,
    private val termsRepo: TermsRepo,
    private val configRepo: ConfigRepo,
    private val flagRepo: FlagRepo,
    private val authRepo: AuthRepo,
    private val topicsFeature: TopicsFeature,
    private val localFeature: LocalFeature,
    private val searchFeature: SearchFeature,
    private val visitedFeature: Visited,
    private val chatFeature: ChatFeature,
    private val analyticsClient: AnalyticsClient,
    private val notificationsRepo: NotificationsRepo
) : ViewModel() {

    private val _uiState: MutableStateFlow<AppUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _homeWidgets: MutableStateFlow<List<HomeWidget>?> = MutableStateFlow(null)
    internal val homeWidgets = _homeWidgets.asStateFlow()

    enum class TimeoutEvent {
        WARNING, TIMEOUT
    }

    private val _timeOutEvent = Channel<TimeoutEvent>(Channel.CONFLATED)
    val timeOutEvent = _timeOutEvent.receiveAsFlow()

    sealed interface NavigationEvent {
        object NavigateToLogin : NavigationEvent
        object NavigateToNotificationsConsent : NavigationEvent
        object NavigateToTerms : NavigationEvent
        object NavigateToAnalytics : NavigationEvent
        object NavigateToTopicSelection : NavigationEvent
        object NavigateToNotificationsOnboarding : NavigationEvent
        object NavigateToNotificationsConsentOnNext : NavigationEvent
        object NavigateToHome : NavigationEvent
    }

    private val _navigationEvent = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        analyticsClient.isUserSessionActive = { authRepo.isUserSessionActive() }

        viewModelScope.launch {
            initWithConfig()
        }
    }

    private suspend fun initWithConfig() {
        val configResult = configRepo.initConfig()

        // returning users
        if (analyticsClient.isAnalyticsEnabled()) {
            configRepo.activateRemoteConfig()
        }

        when (configResult) {
            is Success -> {
                if (!flagRepo.isAppAvailable()) {
                    _uiState.value = AppUiState.AppUnavailable
                } else if (flagRepo.isForcedUpdate(BuildConfig.VERSION_NAME)) {
                    _uiState.value = AppUiState.ForcedUpdate
                } else {
                    topicsFeature.init()

                    combine(
                        appRepo.suppressedHomeWidgets,
                        localFeature.hasLocalAuthority()
                    ) { suppressedWidgets, localAuthority ->
                        Pair(suppressedWidgets, localAuthority)
                    }.collect {
                        _uiState.value = AppUiState.Default(
                            shouldDisplayRecommendUpdate = flagRepo.isRecommendUpdate(BuildConfig.VERSION_NAME),
                            shouldShowExternalBrowser = flagRepo.isExternalBrowserEnabled(),
                            isChatEnabled = flagRepo.isChatEnabled()
                        )

                        updateHomeWidgets(it.first)
                    }
                }
            }
            is InvalidSignature -> _uiState.value = AppUiState.ForcedUpdate
            is DeviceOffline -> _uiState.value = AppUiState.DeviceOffline
            else -> _uiState.value = AppUiState.AppUnavailable
        }
    }

    fun onTryAgain() {
        _uiState.value = AppUiState.Loading
        viewModelScope.launch {
            initWithConfig()
        }
    }

    fun onUserInteraction(
        interactionTime: Long = SystemClock.elapsedRealtime()
    ) {
        timeoutManager.onUserInteraction(
            interactionTime,
            onWarning = {
                viewModelScope.launch {
                    _timeOutEvent.trySend(TimeoutEvent.WARNING)
                }
            },
            onTimeout = {
                if (authRepo.isUserSessionActive()) {
                    authRepo.endUserSession()
                    viewModelScope.launch {
                        _timeOutEvent.trySend(TimeoutEvent.TIMEOUT)
                    }
                }
            }
        )
    }

    fun onLogin() {
        viewModelScope.launch {
            if (authRepo.isDifferentUser()) {
                authRepo.clear()
                appRepo.clear()
                loginRepo.clear()
                termsRepo.clear()
                topicsFeature.clear()
                localFeature.clear()
                searchFeature.clear()
                visitedFeature.clear()
                chatFeature.clear()
                analyticsClient.clear()
                configRepo.clearRemoteConfigValues()
            }
            onNext()
        }
    }

    fun onAnalyticsConsentCompleted() {
        viewModelScope.launch {
            if (analyticsClient.isAnalyticsEnabled()) {
                configRepo.refreshRemoteConfig()
            }
            onNext()
        }
    }

    fun topicSelectionCompleted() {
        viewModelScope.launch {
            appRepo.topicSelectionCompleted()
            onNext()
        }
    }

    private fun updateHomeWidgets(
        suppressedWidgets: Set<String>
    ) {
        viewModelScope.launch {
            with(flagRepo) {
                val widgets = mutableListOf<HomeWidget>()
                if (isSearchEnabled()) {
                    widgets.add(HomeWidget.Search)
                }

                configRepo.emergencyBanners?.forEach { emergencyBanner ->
                    if (!suppressedWidgets.contains(emergencyBanner.id)) {
                        widgets.add(HomeWidget.Banner(emergencyBanner = emergencyBanner))
                    }
                }

                configRepo.chatBanner?.let { chatBanner ->
                    if (isChatEnabled() &&
                        !suppressedWidgets.contains(chatBanner.id)) {
                        widgets.add(HomeWidget.Chat(chatBanner))
                    }
                }

                if (isTopicsEnabled()) {
                    widgets.add(HomeWidget.Topics)
                }
                if (isLocalServicesEnabled()) {
                    widgets.add(HomeWidget.Local)
                }
                if (isRecentActivityEnabled()) {
                    widgets.add(HomeWidget.RecentActivity)
                }
                configRepo.userFeedbackBanner?.let { userFeedbackBanner ->
                    widgets.add(HomeWidget.UserFeedback(userFeedbackBanner = userFeedbackBanner))
                }
                _homeWidgets.value = widgets
            }
        }
    }

    fun onWidgetClick(
        text: String,
        url: String? = null,
        external: Boolean,
        section: String
    ) {
        analyticsClient.widgetClick(
            text,
            url,
            external,
            section
        )
    }

    fun onSuppressWidgetClick(
        id: String,
        text: String,
        section: String
    ) {
        viewModelScope.launch {
            appRepo.suppressHomeWidget(id)
        }
        analyticsClient.suppressWidgetClick(
            text,
            section
        )
    }

    fun onTabClick(text: String) {
        analyticsClient.tabClick(text)
    }

    fun onDeepLinkReceived(hasDeepLink: Boolean, url: String) {
        analyticsClient.deepLinkEvent(
            hasDeepLink,
            url
        )
    }

    fun onNext() {
        viewModelScope.launch {
            when {
                isTermsAcceptanceRequired() ->
                    _navigationEvent.trySend(NavigationEvent.NavigateToTerms)

                isAnalyticsConsentRequired() ->
                    _navigationEvent.trySend(NavigationEvent.NavigateToAnalytics)

                isTopicSelectionRequired() ->
                    _navigationEvent.trySend(NavigationEvent.NavigateToTopicSelection) // <-- Updated!

                isNotificationsOnboardingRequired() ->
                    _navigationEvent.trySend(NavigationEvent.NavigateToNotificationsOnboarding)

                isNotificationsConsentRequired() ->
                    _navigationEvent.trySend(NavigationEvent.NavigateToNotificationsConsentOnNext)

                else ->
                    _navigationEvent.trySend(NavigationEvent.NavigateToHome)
            }
        }
    }

    private suspend fun isTermsAcceptanceRequired() =
        termsRepo.getTermsAcceptanceState() !is TermsAcceptanceState.Accepted

    private fun isAnalyticsConsentRequired() =
        analyticsClient.isAnalyticsConsentRequired()

    private suspend fun isTopicSelectionRequired() =
        flagRepo.isTopicsEnabled() &&
                !appRepo.isTopicSelectionCompleted() &&
                topicsFeature.hasTopics()

    private suspend fun isNotificationsOnboardingRequired() =
        flagRepo.isNotificationsEnabled() &&
                !notificationsRepo.isNotificationsOnboardingCompleted()

    private suspend fun isNotificationsConsentRequired() =
        flagRepo.isNotificationsEnabled() &&
                notificationsRepo.isNotificationsOnboardingCompleted() &&
                notificationsRepo.permissionGranted() &&
                !notificationsRepo.consentGiven()


    fun onResume(currentRoute: String?) {
        viewModelScope.launch {
            if (!authRepo.isUserSessionActive()) {
                _navigationEvent.trySend(NavigationEvent.NavigateToLogin)
            }

            if (flagRepo.isNotificationsEnabled()) {
                handleNotificationsOnResume(currentRoute)
            }
        }
    }

    fun onNotificationsOnboardingCompleted() {
        viewModelScope.launch {
            notificationsRepo.notificationsOnboardingCompleted()
            onNext()
        }
    }

    private suspend fun handleNotificationsOnResume(currentRoute: String?) {
        if (!notificationsRepo.permissionGranted()) {
            notificationsRepo.removeConsent()

            if (currentRoute == NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE) {
                onNext()
            }
        } else if (
            authRepo.isUserSessionActive() &&
            notificationsRepo.isNotificationsOnboardingCompleted() &&
            !notificationsRepo.consentGiven()
        ) {
            _navigationEvent.trySend(NavigationEvent.NavigateToNotificationsConsent)
        }
    }
}
