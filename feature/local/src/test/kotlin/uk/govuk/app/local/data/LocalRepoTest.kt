package uk.govuk.app.local.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.LocalAuthorityResponse
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.Addresses
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult.LocalAuthority
import uk.govuk.app.local.data.remote.model.RemoteAddress
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.domain.toAddress
import uk.govuk.app.local.domain.toLocalAuthority

class LocalRepoTest {
    private val localApi = mockk<LocalApi>(relaxed = true)
    private val localDataSource = mockk<LocalDataSource>(relaxed = true)
    private val apiResponse = mockk<Response<LocalAuthorityResponse>>()
    private val body = mockk<ResponseBody>(relaxed = true)
    private lateinit var localRepo: LocalRepo

    @Before
    fun setup() {
        localRepo = LocalRepo(localApi, localDataSource)
    }

    @Test
    fun `Fetch local authority returns local authority, cache local authority and return result`() = runTest {
        val remoteLocalAuthority = RemoteLocalAuthority(
            name = "name",
            homePageUrl = "url",
            tier = "tier",
            slug = "slug"
        )

        coEvery {
            localApi.fromPostcode(any())
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.isSuccessful } returns true
        every { apiResponse.body() } returns LocalAuthorityResponse(remoteLocalAuthority, null)

        val result = localRepo.fetchLocalAuthority("postcode")
        val expected = Success(LocalAuthority(remoteLocalAuthority))

        assertEquals(expected, result)
        assertEquals(remoteLocalAuthority.toLocalAuthority(), localRepo.cachedLocalAuthority)
    }

    @Test
    fun `Fetch local authority returns addresses, cache addresses and local authorities and return result`() = runTest {
        val remoteAddresses = listOf(
            RemoteAddress("address 1", "slug 1", "name 1"),
            RemoteAddress("address 2", "slug 2", "name 2")
        )

        val remoteLocalAuthority1 = RemoteLocalAuthority(
            name = "name 1",
            homePageUrl = "url 1",
            tier = "tier 1",
            slug = "slug 1"
        )

        val remoteLocalAuthority2 = RemoteLocalAuthority(
            name = "name 2",
            homePageUrl = "url 2",
            tier = "tier 2",
            slug = "slug 2"
        )

        coEvery {
            localApi.fromPostcode(any())
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.isSuccessful } returns true
        every { apiResponse.body() } returns LocalAuthorityResponse(null, remoteAddresses)

        coEvery {
            localApi.fromSlug("slug 1")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority1, addresses = null)
        )
        coEvery {
            localApi.fromSlug("slug 2")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority2, addresses = null)
        )

        val result = localRepo.fetchLocalAuthority("postcode")
        val expected = Success(Addresses(remoteAddresses))

        assertEquals(expected, result)
        assertEquals(remoteAddresses.map { it.toAddress() }, localRepo.addresses)
        assertEquals(
            listOf(remoteLocalAuthority1, remoteLocalAuthority2).map { it.toLocalAuthority() },
            localRepo.localAuthorities
        )
    }

    @Test
    fun `Fetch local authority returns empty addresses, cache addresses and local authorities and return result`() = runTest {
        coEvery {
            localApi.fromPostcode(any())
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.isSuccessful } returns true
        every { apiResponse.body() } returns LocalAuthorityResponse(null, emptyList())

        val result = localRepo.fetchLocalAuthority("postcode")
        val expected = Success(Addresses(emptyList()))

        assertEquals(expected, result)
        assertTrue(localRepo.addresses.isEmpty())
        assertTrue(localRepo.localAuthorities.isEmpty())
    }

    @Test
    fun `Fetch local authority returns addresses but slug lookup fails, cache addresses and local authorities and return result`() = runTest {
        val remoteAddresses = listOf(
            RemoteAddress("address 1", "slug 1", "name 1"),
            RemoteAddress("address 2", "slug 2", "name 2")
        )

        val remoteLocalAuthority1 = RemoteLocalAuthority(
            name = "name 1",
            homePageUrl = "url 1",
            tier = "tier 1",
            slug = "slug 1"
        )

        coEvery {
            localApi.fromPostcode(any())
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.isSuccessful } returns true
        every { apiResponse.body() } returns LocalAuthorityResponse(null, remoteAddresses)

        coEvery {
            localApi.fromSlug("slug 1")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority1, addresses = null)
        )
        coEvery {
            localApi.fromSlug("slug 2")
        } returns Response.error(404, body)

        val result = localRepo.fetchLocalAuthority("postcode")
        val expected = Success(Addresses(remoteAddresses))

        assertEquals(expected, result)
        assertEquals(remoteAddresses.map { it.toAddress() }, localRepo.addresses)
        assertEquals(
            listOf(remoteLocalAuthority1).map { it.toLocalAuthority() },
            localRepo.localAuthorities
        )
    }

    @Test
    fun `Fetch local authority returns addresses but slug lookup returns null local authority, cache addresses and local authorities and return result`() = runTest {
        val remoteAddresses = listOf(
            RemoteAddress("address 1", "slug 1", "name 1"),
            RemoteAddress("address 2", "slug 2", "name 2")
        )

        val remoteLocalAuthority1 = RemoteLocalAuthority(
            name = "name 1",
            homePageUrl = "url 1",
            tier = "tier 1",
            slug = "slug 1"
        )

        coEvery {
            localApi.fromPostcode(any())
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.isSuccessful } returns true
        every { apiResponse.body() } returns LocalAuthorityResponse(null, remoteAddresses)

        coEvery {
            localApi.fromSlug("slug 1")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority1, addresses = null)
        )
        coEvery {
            localApi.fromSlug("slug 2")
        } returns Response.success(
            LocalAuthorityResponse(localAuthority = null, addresses = null)
        )

        val result = localRepo.fetchLocalAuthority("postcode")
        val expected = Success(Addresses(remoteAddresses))

        assertEquals(expected, result)
        assertEquals(remoteAddresses.map { it.toAddress() }, localRepo.addresses)
        assertEquals(
            listOf(remoteLocalAuthority1).map { it.toLocalAuthority() },
            localRepo.localAuthorities
        )
    }

    @Test
    fun `Fetch local authority returns an error, return result`() = runTest {
        coEvery {
            localApi.fromPostcode(any())
        } returns apiResponse

        every { apiResponse.code() } returns 500
        every { apiResponse.isSuccessful } returns false

        assertTrue(localRepo.fetchLocalAuthority("postcode") is Result.Error)
    }

    @Test
    fun `Cache local authority caches local authority and select updates local data source`() = runTest {
        val remoteAddresses = listOf(
            RemoteAddress("address 1", "slug 1", "name 1"),
            RemoteAddress("address 2", "slug 2", "name 2")
        )

        val remoteLocalAuthority1 = RemoteLocalAuthority(
            name = "name 1",
            homePageUrl = "url 1",
            tier = "tier 1",
            slug = "slug 1"
        )

        val remoteLocalAuthority2 = RemoteLocalAuthority(
            name = "name 2",
            homePageUrl = "url 2",
            tier = "tier 2",
            slug = "slug 2"
        )

        coEvery {
            localApi.fromPostcode(any())
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.isSuccessful } returns true
        every { apiResponse.body() } returns LocalAuthorityResponse(null, remoteAddresses)

        coEvery {
            localApi.fromSlug("slug 1")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority1, addresses = null)
        )
        coEvery {
            localApi.fromSlug("slug 2")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority2, addresses = null)
        )

        localRepo.fetchLocalAuthority("postcode")
        localRepo.cacheLocalAuthority("slug 2")

        val expected = remoteLocalAuthority2.toLocalAuthority()

        assertEquals(expected, localRepo.cachedLocalAuthority)

        localRepo.selectLocalAuthority()
        coVerify {
            localDataSource.insertOrReplace(expected)
        }
    }

    @Test
    fun `Clear clears local data source`() {
        runTest {
            localRepo.clear()

            coVerify { localDataSource.clear() }
        }
    }
}
