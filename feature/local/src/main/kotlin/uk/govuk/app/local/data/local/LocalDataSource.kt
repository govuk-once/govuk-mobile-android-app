package uk.govuk.app.local.data.local

import kotlinx.coroutines.flow.Flow
import uk.govuk.app.local.data.local.model.LocalAuthorityEntity
import uk.govuk.app.local.data.local.model.LocalAuthorityParentEntity
import uk.govuk.app.local.domain.model.LocalAuthority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalDataSource @Inject constructor(
    private val dao: LocalDao
) {
    val localAuthority: Flow<LocalAuthorityEntity?> get() = dao.getLocalAuthority()

    suspend fun insertOrReplace(localAuthority: LocalAuthority) {
        dao.insertOrReplace(
            LocalAuthorityEntity(
                name = localAuthority.name,
                url = localAuthority.url,
                slug = localAuthority.slug,
                parent = localAuthority.parent?.let {
                    LocalAuthorityParentEntity(
                        name = it.name,
                        url = it.url,
                        slug = it.slug
                    )
                }
            )
        )
    }

    suspend fun clear() {
        dao.deleteAll()
    }
}
