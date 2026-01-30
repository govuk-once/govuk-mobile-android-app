package uk.gov.govuk.data.flex

import uk.gov.govuk.data.flex.model.FlexResponse
import uk.gov.govuk.data.flex.remote.FlexApi
import uk.gov.govuk.data.flex.remote.safeFlexApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlexRepo @Inject constructor(
    private val flexApi: FlexApi
) {
    suspend fun getFlexPreferences(): FlexResult<FlexResponse> {
        return safeFlexApiCall(apiCall = { flexApi.getFlexPreferences() })
    }
}
