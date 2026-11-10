package uk.govuk.app.local.data

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult
import uk.govuk.app.local.data.remote.model.RemoteAddress
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.data.remote.safeLocalApiCall
import uk.govuk.app.local.domain.model.Address
import uk.govuk.app.local.domain.model.LocalAuthority
import uk.govuk.app.local.domain.toAddress
import uk.govuk.app.local.domain.toLocalAuthority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalRepo @Inject constructor(
    private val localApi: LocalApi,
    private val localDataSource: LocalDataSource
) {
    private val mutex = Mutex()

    val localAuthority = localDataSource.localAuthority.map {
        it?.toLocalAuthority()
    }

    private lateinit var _cachedLocalAuthority: LocalAuthority
    val cachedLocalAuthority
        get() = _cachedLocalAuthority

    private var _addresses: List<Address> = emptyList()
    val addresses: List<Address>
        get() = _addresses

    private var _localAuthorities: List<LocalAuthority> = emptyList()
    val localAuthorities: List<LocalAuthority>
        get() = _localAuthorities.toList()

    suspend fun fetchLocalAuthority(
        postcode: String
    ): Result<LocalAuthorityResult> {
        mutex.withLock {
            val result = safeLocalApiCall { localApi.fromPostcode(postcode) }
            if (result is Result.Success) {
                when (val value = result.value) {
                    is LocalAuthorityResult.Addresses -> cacheAddresses(value.addresses)
                    is LocalAuthorityResult.LocalAuthority ->
                        _cachedLocalAuthority = value.localAuthority.toLocalAuthority()
                    else -> { /* Do nothing */ }
                }
            }
            return result
        }
    }

    private suspend fun cacheAddresses(
        addresses: List<RemoteAddress>
    ) {
        val slugs = addresses.distinctBy { it.slug }.map { it.slug }

        val localAuthorities = mutableListOf<RemoteLocalAuthority>()
        slugs.forEach { slug ->
            safeLocalApiCall { localApi.fromSlug(slug) }.let { result ->
                (result as? Result.Success)?.value?.let { value ->
                    (value as? LocalAuthorityResult.LocalAuthority)?.localAuthority?.let { localAuthority ->
                        localAuthorities += localAuthority
                    }
                }
            }
        }

        _addresses = addresses.map { it.toAddress() }
        _localAuthorities = localAuthorities.map { it.toLocalAuthority() }
    }

    fun cacheLocalAuthority(slug: String) {
        localAuthorities.find { it.slug == slug }?.let { localAuthority ->
            _cachedLocalAuthority = localAuthority
        }
    }

    suspend fun selectLocalAuthority() {
        localDataSource.insertOrReplace(cachedLocalAuthority)
    }

    suspend fun clear() {
        localDataSource.clear()
    }
}
