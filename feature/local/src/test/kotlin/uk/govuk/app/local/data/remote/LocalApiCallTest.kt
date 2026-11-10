package uk.govuk.app.local.data.remote

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.remote.model.RemoteAddress
import uk.govuk.app.local.data.remote.model.LocalAuthorityResponse
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import java.net.UnknownHostException

class LocalApiCallTest {
    private val localApi = mockk<LocalApi>(relaxed = true)
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `API call returns 200 - local authority`() = runTest {
        val remoteLocalAuthority = RemoteLocalAuthority(
            name = "Dorset County Council",
            homePageUrl = "",
            tier = "tier",
            slug = "dorset",
            parent = null
        )

        coEvery {
            localApi.fromPostcode("E18QS")
        } returns Response.success(
            LocalAuthorityResponse(
                remoteLocalAuthority,
                addresses = null
            )
        )

        val actual = safeLocalApiCall { localApi.fromPostcode("E18QS") }

        assertEquals(
            Result.Success(
                LocalAuthorityResult.LocalAuthority(remoteLocalAuthority)
            ),
            actual
        )
    }

    @Test
    fun `API call returns 200 - address list`() = runTest {
        val addresses = listOf(
            RemoteAddress(
                address = "BH22 8UB",
                slug = "dorset",
                name = "Dorset County Council"
            ),
            RemoteAddress(
                address = "BH22 8UB",
                slug = "bournemouth-christchurch-poole",
                name = "Bournemouth, Christchurch, and Poole"
            )
        )

        coEvery {
            localApi.fromPostcode("BH228UB")
        } returns Response.success(
            LocalAuthorityResponse(
                localAuthority = null,
                addresses = addresses
            )
        )

        val actual = safeLocalApiCall { localApi.fromPostcode("BH228UB") }

        assertEquals(
            Result.Success(
                LocalAuthorityResult.Addresses(addresses)
            ),
            actual
        )
    }

    @Test
    fun `API call returns Error`() = runTest {
        coEvery {
            localApi.fromPostcode("E18QS")
        } returns Response.success(
            LocalAuthorityResponse(
                localAuthority = null,
                addresses = null
            )
        )

        val actual = safeLocalApiCall { localApi.fromPostcode("E18QS") }

        assertEquals("Error", actual.javaClass.kotlin.simpleName)
    }

    @Test
    fun `API call returns 400`() = runTest {
        val apiResponse = LocalAuthorityResult.InvalidPostcode

        coEvery {
            localApi.fromPostcode("E18QS")
        } returns Response.error(400, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.fromPostcode("E18QS") }

        assertEquals(Result.Success(apiResponse), actual)
    }

    @Test
    fun `API call returns 404`() = runTest {
        val apiResponse = LocalAuthorityResult.PostcodeNotFound

        coEvery {
            localApi.fromPostcode("E18QS")
        } returns Response.error(404, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.fromPostcode("E18QS") }

        assertEquals(Result.Success(apiResponse), actual)
    }

    @Test
    fun `API call returns 418`() = runTest {
        val apiResponse = LocalAuthorityResult.PostcodeEmptyOrNull

        coEvery {
            localApi.fromPostcode("")
        } returns Response.error(418, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.fromPostcode("") }

        assertEquals(Result.Success(apiResponse), actual)
    }

    @Test
    fun `API call returns 429`() = runTest {
        val apiResponse = LocalAuthorityResult.ApiNotResponding

        coEvery {
            localApi.fromPostcode("")
        } returns Response.error(429, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.fromPostcode("") }

        assertEquals(Result.Success(apiResponse), actual)
    }

    @Test
    fun `API call returns 500`() = runTest {
        coEvery {
            localApi.fromPostcode("E18QS")
        } returns Response.error(500, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.fromPostcode("E18QS") }

        assertEquals("Error", actual.javaClass.kotlin.simpleName)
    }

    @Test
    fun `API call throws UnknownHostException`() = runTest {
        val apiResponse = LocalAuthorityResult.DeviceNotConnected

        coEvery {
            localApi.fromPostcode("E18QS")
        } throws UnknownHostException()

        val actual = safeLocalApiCall { localApi.fromPostcode("E18QS") }

        assertEquals(Result.Success(apiResponse), actual)
    }

    @Test
    fun `API call throws HttpException`() = runTest {
        val apiResponse = LocalAuthorityResult.ApiNotResponding

        coEvery {
            localApi.fromPostcode("E18QS")
        } throws retrofit2.HttpException(
            Response.error<Any>(
                500,
                "Error".toResponseBody(null)
            )
        )

        val actual = safeLocalApiCall { localApi.fromPostcode("E18QS") }

        assertEquals(Result.Success(apiResponse), actual)
    }
}
