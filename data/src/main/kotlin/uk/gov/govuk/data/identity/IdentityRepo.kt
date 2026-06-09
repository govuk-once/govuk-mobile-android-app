package uk.gov.govuk.data.identity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.identity.model.IdentityState
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.data.identity.remote.IdentityApi
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.data.model.Result
import javax.inject.Inject

class IdentityRepo @Inject constructor(
    private val api: IdentityApi,
    private val authRepo: AuthRepo
) {

    private val _state = MutableStateFlow<IdentityState>(IdentityState.Checking)
    val state = _state.asStateFlow()

    suspend fun fetchLinkedServices() {
        _state.value = IdentityState.Checking

        val result = safeAuthApiCall({ api.getLinkedServices() }, authRepo)

        if (result is Result.Success) {
            // map to LinkedService
            val services = result.value.services.mapNotNull { service ->
                LinkedService.entries.find { it.serviceName == service }
            }

            _state.value = IdentityState.Success(services)
        } else {
            _state.value = IdentityState.Error
        }
    }
}