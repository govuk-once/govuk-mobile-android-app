package uk.gov.govuk.settings.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.identity.IdentityRepo
import uk.gov.govuk.data.identity.model.IdentityState
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.settings.domain.LinkedAccountsRepo
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel
import javax.inject.Inject

class LinkedAccountsRepoImpl @Inject constructor(
    private val identityRepo: IdentityRepo,
    private val dvlaRepo: DvlaRepo,
    private val flagRepo: FlagRepo
): LinkedAccountsRepo {


    override fun getLinkedAccounts(): Flow<List<LinkedAccountUiModel>> {
        return identityRepo.state.map { state ->
            val accounts = mutableListOf<LinkedAccountUiModel>()

            if (state is IdentityState.Success) {
                // any future linked service would be checked here
                if (state.services.contains(LinkedService.DVLA) && flagRepo.isDvlaLinkEnabled()) {
                    accounts.add(
                        LinkedAccountUiModel(
                            serviceName = LinkedService.DVLA.serviceName,
                            displayTitleRes = uk.gov.govuk.dvla.R.string.dvla_account_title
                        )
                    )
                }
            }

            accounts
        }
    }

    override suspend fun unlinkAccount(serviceName: String): Result<Unit> {
        return when (serviceName) {
            LinkedService.DVLA.serviceName -> dvlaRepo.unlinkAccount()
            else -> Result.Success(Unit)
        }
    }
}