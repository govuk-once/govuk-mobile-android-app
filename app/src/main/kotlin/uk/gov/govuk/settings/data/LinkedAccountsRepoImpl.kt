package uk.gov.govuk.settings.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.settings.domain.LinkedAccountsRepo
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel
import javax.inject.Inject

// names/IDs for the services we support linking with. The names used are the ones FLEX uses.
// Add any new service here.
private const val SERVICE_NAME_DVLA = "dvla"

class LinkedAccountsRepoImpl @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val flagRepo: FlagRepo
): LinkedAccountsRepo {

    override fun getLinkedAccounts(): Flow<List<LinkedAccountUiModel>> {
        // any future linked service would be checked here
        return dvlaRepo.linkState.map { linkState ->
            val accounts = mutableListOf<LinkedAccountUiModel>()

            if (linkState == DvlaLinkState.LINKED && flagRepo.isDvlaLinkEnabled()) {
                accounts.add(
                    LinkedAccountUiModel(
                        serviceName = SERVICE_NAME_DVLA,
                        displayTitleRes = uk.gov.govuk.dvla.R.string.dvla_account_title
                    )
                )
            }
            accounts
        }
    }

    override suspend fun unlinkAccount(serviceName: String): Result<Unit> {
        return when (serviceName) {
            SERVICE_NAME_DVLA -> dvlaRepo.unlinkAccount()
            else -> Result.Success(Unit)
        }
    }
}