package uk.gov.govuk.sar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeApiCall
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.data.user.remote.UserApi
import uk.gov.govuk.sar.data.SubjectAccessRequestFile
import javax.inject.Inject

@HiltViewModel
internal class SubjectAccessRequestViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    @ApplicationContext private val context: Context,
    private val userApi: UserApi
): ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            val file = SubjectAccessRequestFile(context)
            _userProfile.value = file.readUserData()
        }
    }

    fun saveUserData(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch {
            val result = safeApiCall(apiCall = { userApi.getUserInfo() })
            val file = SubjectAccessRequestFile(context, dispatcher)

            // TODO: Handle errors
            if (result is Result.Success) {
                file.writeUserData(result.value)
            }
        }
    }

    companion object {
        private const val EXPLAINER_SCREEN_CLASS = "SubjectAccessRequestExplainerScreen"
        private const val EXPLAINER_SCREEN_NAME = "Subject Access Request Explainer"
        private const val EXPLAINER_TITLE = "Subject Access Request Explainer"
        private const val DISPLAY_SCREEN_CLASS = "SubjectAccessRequestDisplayScreen"
        private const val DISPLAY_SCREEN_NAME = "Subject Access Request Display"
        private const val DISPLAY_TITLE = "Subject Access Request Display"
        private const val SECTION = "Subject Access Request"
    }

    fun onExplainerPageView() {
        analyticsClient.screenView(
            screenClass = EXPLAINER_SCREEN_CLASS,
            screenName = EXPLAINER_SCREEN_NAME,
            title = EXPLAINER_TITLE
        )
    }

    fun onDisplayPageView() {
        analyticsClient.screenView(
            screenClass = DISPLAY_SCREEN_CLASS,
            screenName = DISPLAY_SCREEN_NAME,
            title = DISPLAY_TITLE
        )
    }

    fun onButtonClick(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }
}
