package uk.gov.govuk.config.data.remote.source

import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.data.model.Result
import java.net.UnknownHostException

class GovUkConfigDataSourceTest {

    private val configApi = mockk<ConfigApi>(relaxed = true)
    private val gson = mockk<Gson>(relaxed = true)
    private val signatureValidator = mockk<SignatureValidator>(relaxed = true)

    // Test Helpers
    private val config = mockk<Config>(relaxed = true)
    private val configResponse = mockk<ConfigResponse>(relaxed = true)
    private val response = mockk<Response<String>>(relaxed = true)
    private val dataSource = GovUkConfigDataSource(configApi, gson, signatureValidator)

    @Test
    fun `Given a successful config response with a body, then return success`() = runTest {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")

        val result = dataSource.fetchConfig()
        assertTrue(result is Result.Success)
        assertEquals(config, (result as Result.Success).value)
    }

    @Test
    fun `Given a successful config response with an empty body, then return failure`() = runTest {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns null

        assertTrue(dataSource.fetchConfig() is Result.Error)
    }

    @Test
    fun `Given an unsuccessful config response, then return failure`() = runTest {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns false

        assertTrue(dataSource.fetchConfig() is Result.Error)
    }

    @Test
    fun `Given an unknown host exception is thrown fetching the config response, then return device offline failure`() = runTest {
        coEvery { configApi.getConfig() } throws UnknownHostException()

        assertTrue(dataSource.fetchConfig() is Result.DeviceOffline)
    }

    @Test
    fun `Given an invalid signature, when config is requested, then return failure`() = runTest {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns false

        assertTrue(dataSource.fetchConfig() is Result.InvalidSignature)
    }

    @Test
    fun `Given a response with a signature header, the specific signature is extracted`() = runTest {
        val specificSignature = "signature-123"
        val headers = Headers.headersOf("x-amz-meta-govuk-sig", specificSignature)

        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString(), headers)
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "sig")

        dataSource.fetchConfig()

        coVerify { signatureValidator.isValidSignature(specificSignature, any()) }
    }

    @Test
    fun `Given a response without a signature header, signature defaults to empty string`() = runTest {
        val headers = Headers.headersOf()
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString(), headers)
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "sig")

        dataSource.fetchConfig()

        coVerify { signatureValidator.isValidSignature("", any()) }
    }
}