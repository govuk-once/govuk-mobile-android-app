package uk.gov.govuk.settings.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel

interface LinkedAccountsRepo {
    fun getLinkedAccounts(): Flow<List<LinkedAccountUiModel>>
    suspend fun unlinkAccount(
        serviceName: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Result<Unit>
}
