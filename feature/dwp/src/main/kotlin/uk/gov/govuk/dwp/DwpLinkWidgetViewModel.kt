package uk.gov.govuk.dwp

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.dwp.data.DwpLinkRepo

@HiltViewModel
internal class DwpLinkWidgetViewModel @Inject constructor(
    dwpLinkRepo: DwpLinkRepo
) : ViewModel() {
    val dwpState: Flow<ServiceLinkStatus> = dwpLinkRepo.linkState
}
