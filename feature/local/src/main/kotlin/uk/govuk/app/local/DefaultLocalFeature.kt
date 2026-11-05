package uk.govuk.app.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.govuk.app.local.data.LocalRepo
import javax.inject.Inject

internal class DefaultLocalFeature @Inject constructor(
    private val localRepo: LocalRepo
): LocalFeature {

    override fun hasLocalAuthority(): Flow<Boolean> {
        return localRepo.localAuthority.map { it != null }
    }

    override suspend fun clear() {
        localRepo.clear()
    }

}