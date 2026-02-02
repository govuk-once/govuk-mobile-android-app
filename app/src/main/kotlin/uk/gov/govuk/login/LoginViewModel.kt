package uk.gov.govuk.login

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.R
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.auth.ErrorEvent
import uk.gov.govuk.login.data.LoginRepo
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.data.model.Result.Success
import java.util.Date
import javax.inject.Inject

internal data class LoginEvent(val isBiometricLogin: Boolean)

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val loginRepo: LoginRepo,
    private val configRepo: ConfigRepo,
    private val notificationsRepo: NotificationsRepo
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isLoading = _isLoading.asStateFlow()

    private val _loginCompleted = MutableSharedFlow<LoginEvent>()
    val loginCompleted: SharedFlow<LoginEvent> = _loginCompleted

    private val _errorEvent = MutableSharedFlow<ErrorEvent>()
    val errorEvent: SharedFlow<ErrorEvent> = _errorEvent

    val authIntent: Intent by lazy {
        authRepo.authIntent
    }

    fun init(activity: FragmentActivity) {
        if (authRepo.isUserSignedIn()) {
            viewModelScope.launch {
                if (shouldRefreshTokens()) {
                    authRepo.refreshTokens(activity = activity, title = activity.getString(R.string.login_biometric_prompt_title)).collect { status ->
                        when (status) {
                            AuthRepo.RefreshStatus.LOADING -> {
                                _isLoading.value = true
                            }
                            AuthRepo.RefreshStatus.SUCCESS -> {
                                val result = notificationsRepo.login()
                                when (result) {
                                    is Success -> _loginCompleted.emit(
                                        LoginEvent(isBiometricLogin = true)
                                    )
                                    else -> _errorEvent.emit(ErrorEvent.UserApiError)
                                }
                            }
                            AuthRepo.RefreshStatus.ERROR -> {
                                _isLoading.value = false
                            }
                        }
                    }
                } else {
                    authRepo.endUserSession()
                    authRepo.clear()
                }
            }
        }
    }

    fun onAuthResponse(data: Intent?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepo.handleAuthResponse(data)
            if (result) {
                saveRefreshTokenIssuedAtDate()
                val result = notificationsRepo.login()
                when (result) {
                    is Success -> _loginCompleted.emit(
                        LoginEvent(isBiometricLogin = false)
                    )
                    else -> _errorEvent.emit(ErrorEvent.UserApiError)
                }
            } else {
                _errorEvent.emit(ErrorEvent.UnableToSignInError)
            }
        }
    }

    private suspend fun shouldRefreshTokens(): Boolean {
        val tokenExpirySeconds = getTokenExpirySeconds()
        return tokenExpirySeconds == null || tokenExpirySeconds > Date().toInstant().epochSecond
    }

    private suspend fun getTokenExpirySeconds(): Long? {
        val issuedAtDate = loginRepo.getRefreshTokenIssuedAtDate()
        val expirySeconds = configRepo.refreshTokenExpirySeconds
        return if (issuedAtDate != null && expirySeconds != null) {
            issuedAtDate + expirySeconds
        } else {
            loginRepo.getRefreshTokenExpiryDate()
        }
    }

    private suspend fun saveRefreshTokenIssuedAtDate() {
        authRepo.getIdTokenIssuedAtDate()?.let { idTokenIssuedAtDate ->
            loginRepo.setRefreshTokenIssuedAtDate(idTokenIssuedAtDate)
        }
    }
}
