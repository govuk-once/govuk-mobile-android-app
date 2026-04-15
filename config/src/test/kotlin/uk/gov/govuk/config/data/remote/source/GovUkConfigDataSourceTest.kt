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
import uk.gov.govuk.config.data.remote.ContentApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.config.data.remote.model.TermsAndConditions
import uk.gov.govuk.config.data.remote.model.TermsAndConditionsTimestamp
import uk.gov.govuk.data.model.Result
import java.net.UnknownHostException

class GovUkConfigDataSourceTest {

    private val configApi = mockk<ConfigApi>(relaxed = true)
    private val contentApi = mockk<ContentApi>(relaxed = true)
    private val gson = mockk<Gson>(relaxed = true)
    private val signatureValidator = mockk<SignatureValidator>(relaxed = true)

    // Test Helpers
    private val config = mockk<Config>(relaxed = true)
    private val configResponse = mockk<ConfigResponse>(relaxed = true)
    private val response = mockk<Response<String>>(relaxed = true)
    private val contentResponse = mockk<Response<String>>(relaxed = true)
    private val dataSource = GovUkConfigDataSource(configApi, contentApi, gson, signatureValidator)

    @Test
    fun `Given a successful config response with a body, then return success`() = runTest {
        val remoteTimestamp = "2026-01-01T00:00:00Z"
        val termsAndConditions = TermsAndConditions(
            lastUpdated = "old-timestamp",
            url = "url",
            contentItemApiUrl = "contentItemUrl"
        )
        val config = Config(
            available = true,
            minimumVersion = "1.0.0",
            recommendedVersion = "1.1.0",
            releaseFlags = mockk(relaxed = true),
            version = "1.0.0",
            chatPollIntervalSeconds = 3.0,
            userFeedbackBanner = null,
            chatUrls = mockk(relaxed = true),
            refreshTokenExpirySeconds = 3600,
            emergencyBanners = null,
            chatBanner = null,
            termsAndConditions = termsAndConditions
        )

        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { contentApi.getContent(url = any()) } returns Response.success(contentResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "sig")
        coEvery { gson.fromJson(any<String>(), TermsAndConditionsTimestamp::class.java) } returns TermsAndConditionsTimestamp(remoteTimestamp)

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

    @Test
    fun `Given a successful config and terms response, when fetched, then the terms timestamp is updated`() = runTest {
        val remoteTimestamp = "2026-01-01T00:00:00Z"
        val termsAndConditions = TermsAndConditions(
            lastUpdated = "old-timestamp",
            url = "url",
            contentItemApiUrl = "contentItemUrl"
        )
        val config = Config(
            available = true,
            minimumVersion = "1.0.0",
            recommendedVersion = "1.1.0",
            releaseFlags = mockk(relaxed = true),
            version = "1.0.0",
            chatPollIntervalSeconds = 3.0,
            userFeedbackBanner = null,
            chatUrls = mockk(relaxed = true),
            refreshTokenExpirySeconds = 3600,
            emergencyBanners = null,
            chatBanner = null,
            termsAndConditions = termsAndConditions
        )

        coEvery { configApi.getConfig() } returns Response.success("{}")
        coEvery { contentApi.getContent(any()) } returns Response.success("{}")
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "sig")
        coEvery { gson.fromJson(any<String>(), TermsAndConditionsTimestamp::class.java) } returns TermsAndConditionsTimestamp(remoteTimestamp)

        val result = dataSource.fetchConfig() as Result.Success

        assertEquals(remoteTimestamp, result.value.termsAndConditions?.lastUpdated)
    }

    @Test
    fun `Given a successful config but a failed terms response, when fetched, then return failure`() = runTest {
        val termsAndConditions = TermsAndConditions(
            lastUpdated = "old-timestamp",
            url = "url",
            contentItemApiUrl = "contentItemUrl"
        )
        val config = Config(
            available = true,
            minimumVersion = "1.0.0",
            recommendedVersion = "1.1.0",
            releaseFlags = mockk(relaxed = true),
            version = "1.0.0",
            chatPollIntervalSeconds = 3.0,
            userFeedbackBanner = null,
            chatUrls = mockk(relaxed = true),
            refreshTokenExpirySeconds = 3600,
            emergencyBanners = null,
            chatBanner = null,
            termsAndConditions = termsAndConditions
        )

        coEvery { configApi.getConfig() } returns Response.success("{}")
        coEvery { contentApi.getContent(url = any()) } returns Response.error(404, mockk(relaxed = true))
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "sig")

        assertTrue(dataSource.fetchConfig() is Result.Error)
    }

    @Test
    fun `Given a config with no terms and conditions timestamp, when fetched, then the terms timestamp is added`() = runTest {
        val remoteTimestamp = "2026-01-01T00:00:00Z"
        val termsAndConditions = TermsAndConditions(
            url = "url",
            contentItemApiUrl = "contentItemUrl"
        )
        val config = Config(
            available = true,
            minimumVersion = "1.0.0",
            recommendedVersion = "1.1.0",
            releaseFlags = mockk(relaxed = true),
            version = "1.0.0",
            chatPollIntervalSeconds = 3.0,
            userFeedbackBanner = null,
            chatUrls = mockk(relaxed = true),
            refreshTokenExpirySeconds = 3600,
            emergencyBanners = null,
            chatBanner = null,
            termsAndConditions = termsAndConditions
        )

        coEvery { configApi.getConfig() } returns Response.success("{}")
        coEvery { contentApi.getContent(url = any()) } returns Response.success("{}")
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "sig")
        coEvery { gson.fromJson(any<String>(), TermsAndConditionsTimestamp::class.java) } returns TermsAndConditionsTimestamp(remoteTimestamp)

        val result = dataSource.fetchConfig() as Result.Success

        assertEquals(remoteTimestamp, result.value.termsAndConditions?.lastUpdated)
    }

    @Test
    fun `Given a config with no terms and conditions content item url, when fetched, then return failure`() = runTest {
        val remoteTimestamp = "2026-01-01T00:00:00Z"
        val termsAndConditions = TermsAndConditions(
            lastUpdated = "old-timestamp",
            url = "url",
            contentItemApiUrl = ""
        )
        val config = Config(
            available = true,
            minimumVersion = "1.0.0",
            recommendedVersion = "1.1.0",
            releaseFlags = mockk(relaxed = true),
            version = "1.0.0",
            chatPollIntervalSeconds = 3.0,
            userFeedbackBanner = null,
            chatUrls = mockk(relaxed = true),
            refreshTokenExpirySeconds = 3600,
            emergencyBanners = null,
            chatBanner = null,
            termsAndConditions = termsAndConditions
        )

        coEvery { configApi.getConfig() } returns Response.success("{}")
        coEvery { contentApi.getContent(url = null) } returns Response.success("{}")
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "sig")
        coEvery { gson.fromJson(any<String>(), TermsAndConditionsTimestamp::class.java) } returns TermsAndConditionsTimestamp(remoteTimestamp)

        assertTrue(dataSource.fetchConfig() is Result.Error)
    }
}
