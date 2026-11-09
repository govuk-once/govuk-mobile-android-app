package uk.gov.govuk.data.identity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.identity.model.IdentityState
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.data.identity.remote.IdentityApi
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.data.model.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdentityRepo @Inject constructor(
    private val api: IdentityApi,
    private val authRepo: AuthRepo
) {

    private val _state = MutableStateFlow<IdentityState>(IdentityState.Checking)
    val state = _state.asStateFlow()

    suspend fun getLinkedServices() {
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

    // observing the state of a specific service
    fun linkStatusOf(service: LinkedService): Flow<ServiceLinkStatus> {
        // in the future when more services are added, consider filtering for distinct emissions
        return _state.map { state ->
            when (state) {
                is IdentityState.Checking -> ServiceLinkStatus.CHECKING
                is IdentityState.Success -> {
                    if (state.services.contains(service)) ServiceLinkStatus.LINKED
                    else ServiceLinkStatus.UNLINKED
                }
                is IdentityState.Error -> ServiceLinkStatus.ERROR
            }
        }
    }

    // one-off check
    fun currentStatusOf(service: LinkedService): ServiceLinkStatus {
        return when (val state = _state.value) {
            is IdentityState.Checking -> ServiceLinkStatus.CHECKING
            is IdentityState.Success -> {
                if (state.services.contains(service)) ServiceLinkStatus.LINKED else ServiceLinkStatus.UNLINKED
            }
            is IdentityState.Error -> ServiceLinkStatus.UNLINKED
        }
    }
}