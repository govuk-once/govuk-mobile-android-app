package uk.gov.govuk.data.auth

import android.content.Intent
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.ERROR
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.LOADING
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.SUCCESS
import uk.gov.govuk.data.crypto.CryptoProvider
import uk.gov.govuk.data.remote.AuthApi
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.text.toByteArray

class AuthRepoTest {

    private val attestationProvider = mockk<AttestationProvider>(relaxed = true)
    private val authRequest = mockk<AuthorizationRequest>(relaxed = true)
    private val authService = mockk<AuthorizationService>(relaxed = true)
    private val tokenRequestBuilder = mockk<TokenRequest.Builder>(relaxed = true)
    private val tokenResponseMapper = mockk<TokenResponseMapper>(relaxed = true)
    private val secureStore = mockk<SecureStore>(relaxed = true)
    private val biometricManager = mockk<BiometricManager>(relaxed = true)
    private val intent = mockk<Intent>(relaxed = true)
    private val authResponse = mockk<AuthorizationResponse>(relaxed = true)
    private val tokenResponse = mockk<TokenResponse>(relaxed = true)
    private val authApi = mockk<AuthApi>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val tokenRepo = mockk<TokenRepo>(relaxed = true)
    private val cryptoProvider = mockk<CryptoProvider>(relaxed = true)

    private lateinit var authRepo: AuthRepo

    @Before
    fun setup() {
        mockkStatic(AuthorizationResponse::class)

        authRepo = AuthRepo(
            attestationProvider, authRequest, authService, tokenRequestBuilder,
            tokenResponseMapper,
            secureStore,
            biometricManager,
            authApi,
            analyticsClient,
            tokenRepo,
            cryptoProvider
        )
    }

    @After
    fun tearDown() {
        // Remove mocks to prevent side effects
        unmockkAll()
    }

    @Test
    fun `Given an auth response, when handle auth response, then clear`() {
        runTest {
            authRepo.handleAuthResponse(null)
            coVerify { authRepo.clear() }
        }
    }

    @Test
    fun `Given null data, when handle auth response, return false`() {
        runTest {
            assertFalse(authRepo.handleAuthResponse(null))
        }
    }

    @Test
    fun `Given a null auth response, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns null

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given attestation provider returns a null token then do not add header`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        coEvery { attestationProvider.getToken() } returns null

        val slot = slot<ClientAuthentication>()
        every {
            authService.performTokenRequest(
                any(),
                capture(slot),
                any()
            )
        } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        runTest {
            authRepo.handleAuthResponse(intent)

            assertFalse(slot.captured.getRequestHeaders("").containsKey("X-Attestation-Token"))
            assertEquals("1234", slot.captured.getRequestParameters("1234")["client_id"])
            assertEquals("openid%20email", slot.captured.getRequestParameters("1234")["scope"])
        }
    }

    @Test
    fun `Given attestation provider returns a token then add header`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        coEvery { attestationProvider.getToken() } returns "attestation_token"

        val slot = slot<ClientAuthentication>()
        every {
            authService.performTokenRequest(
                any(),
                capture(slot),
                any()
            )
        } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        runTest {
            authRepo.handleAuthResponse(intent)

            assertEquals("attestation_token", slot.captured.getRequestHeaders("")["X-Attestation-Token"])
            assertEquals("1234", slot.captured.getRequestParameters("1234")["client_id"])
            assertEquals("openid%20email", slot.captured.getRequestParameters("1234")["scope"])
        }
    }

    @Test
    fun `Given the token request returns an exception, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(null, AuthorizationException.GeneralErrors.NETWORK_ERROR)
        }

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request returns a null access token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = null,
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request returns a null id token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = null,
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request returns a null refresh token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = null
                )

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request is successful, when handle auth response, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertTrue(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the refresh token is blank, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = " "
                )

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given a successful retrieval, when persist refresh token, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                any(),
                any()
            )
        } returns RetrievalEvent.Success(emptyMap())

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(
                authRepo.persistRefreshToken(activity, "")
            )

            coVerify { secureStore.upsert("refreshToken", "refreshToken") }
        }
    }

    @Test
    fun `Given a failed retrieval, when persist refresh token, return false and delete token`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                any(),
                any()
            )
        } returns RetrievalEvent.Failed(SecureStoreErrorType.GENERAL)

        runTest {
            authRepo.handleAuthResponse(intent)
            assertFalse(
                authRepo.persistRefreshToken(activity, "")
            )

            coVerify { secureStore.upsert("refreshToken", "refreshToken") }
            coVerify { secureStore.delete("refreshToken") }
        }
    }

    @Test
    fun `Given the Android version is 30 and biometric success, when is authentication enabled, return true`() {
        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns  BIOMETRIC_SUCCESS
        val androidVersion = Build.VERSION_CODES.R

        assertTrue(authRepo.isAuthenticationEnabled(androidVersion))
    }

    @Test
    fun `Given the Android version is 29 and biometric success, when is authentication enabled, return true`() {
        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG
            )
        } returns  BIOMETRIC_SUCCESS
        val androidVersion = Build.VERSION_CODES.Q

        assertTrue(authRepo.isAuthenticationEnabled(androidVersion))
    }

    @Test
    fun `Given the Android version is 30 and non biometric success, when is authentication enabled, return false`() {
        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_STATUS_UNKNOWN
        val androidVersion = Build.VERSION_CODES.R

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))
    }

    @Test
    fun `Given the Android version is 29 and non biometric success, when is authentication enabled, return false`() {
        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG
            )
        } returns BiometricManager.BIOMETRIC_STATUS_UNKNOWN
        val androidVersion = Build.VERSION_CODES.Q

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG
            )
        } returns BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG
            )
        } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG
            )
        } returns BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED

        assertFalse(authRepo.isAuthenticationEnabled(androidVersion))
    }

    @Test
    fun `Given the refresh token retrieval returns a null refresh token, when refresh tokens, then clear secure store and return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(emptyMap())

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())

            coVerify {
                secureStore.delete("refreshToken")
                analyticsClient.logException(any())
            }
        }
    }

    @Test
    fun `Given the refresh token retrieval returns a blank refresh token, when refresh tokens, then clear secure store and return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to " "))

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())

            coVerify {
                secureStore.delete("refreshToken")
                analyticsClient.logException(any())
            }
        }
    }

    @Test
    fun `Given the refresh token retrieval is unsuccessful, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Failed(SecureStoreErrorType.GENERAL)

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())
        }
    }

    @Test
    fun `Given the token request returns a non oauth token exception type, when refresh tokens, then do not delete refresh token and return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))


        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(null, AuthorizationException.GeneralErrors.NETWORK_ERROR)
        }

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())
        }

        verify(exactly = 0) {
            secureStore.delete(any())
        }
    }

    @Test
    fun `Given the token request returns an oauth token exception type, when refresh tokens, then delete refresh token and return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(
                null,
                AuthorizationException.TokenRequestErrors.INVALID_GRANT
            )
        }

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())
        }

        verify {
            secureStore.delete("refreshToken")
        }
    }

    @Test
    fun `Given the token request returns a null access token, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = null,
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())
        }
    }

    @Test
    fun `Given the token request returns a blank access token, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = " ",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())
        }
    }

    @Test
    fun `Given the token request returns a null id token, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = null,
                    refreshToken = "refreshToken"
                )

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())
        }
    }

    @Test
    fun `Given the token request returns a blank id token, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = " ",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertEquals(ERROR, authRepo.refreshTokens(activity, "").last())
        }
    }

    @Test
    fun `Given the token request is successful, when refresh tokens, then return true`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = null
                )

        runTest {
            assertEquals(LOADING, authRepo.refreshTokens(activity, "").first())
            assertEquals(SUCCESS, authRepo.refreshTokens(activity, "").last())
        }
    }

    @Test
    fun `Given no exceptions when deleting or revoking, when clear, then delete, revoke and return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(authRepo.clear())
        }

        coVerify {
            secureStore.delete("refreshToken")
            authApi.revoke(any(), any())
        }
    }

    @Test
    fun `Given a blank refresh token, when clear, then delete token from secure store, do not revoke token and return true`() {
        runTest {
            assertTrue(authRepo.clear())
        }

        coVerify {
            secureStore.delete("refreshToken")
        }

        coVerify(exactly = 0) {
            authApi.revoke(any(), any())
        }
    }

    @Test
    fun `Given an exception deleting from secure store, when clear, then do not revoke token and return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )
        every {
            secureStore.delete("refreshToken")
        } throws SecureStorageError(
            type = SecureStoreErrorType.GENERAL,
            exception = Exception()
        )

        runTest {
            authRepo.handleAuthResponse(intent)
            assertFalse(authRepo.clear())
        }

        coVerify {
            secureStore.delete("refreshToken")
        }

        coVerify(exactly = 0) {
            authApi.revoke(any(), any())
        }
    }

    @Test
    fun `Given an exception revoking the token, when clear, then return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )
        coEvery {
            authApi.revoke(any(), any())
        } throws IOException()

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(authRepo.clear())
        }

        coVerify {
            secureStore.delete("refreshToken")
        }
    }

    @Test
    fun `Given a user session is not active, when is user session active, return false`() {
        assertFalse(authRepo.isUserSessionActive())
    }

    @Test
    fun `Given a user session is active, when is user session active, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(authRepo.isUserSessionActive())
        }
    }


    @Test
    fun `Given a user session is ended, when end user session, then user session is no longer active`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(authRepo.isUserSessionActive())
            authRepo.endUserSession()
            assertFalse(authRepo.isUserSessionActive())
        }
    }

    @Test
    fun `Given a request for the access token, return the access token`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
            TokenResponseMapper.Tokens(
                accessToken = "accessToken",
                idToken = "idToken",
                refreshToken = "refreshToken"
            )

        runTest {
            authRepo.handleAuthResponse(intent)
            assertEquals("accessToken", authRepo.getAccessToken())
        }
    }

    @Test
    fun `Given a user is logging in again and has a sub id stored in shared prefs, when isDifferentUser() called, then returns true`() {
        every {
            cryptoProvider.encrypt(any())
        } returns Result.success("54321")
        every {
            cryptoProvider.decrypt("54321")
        } returns Result.success("12345".toByteArray(StandardCharsets.UTF_8))

        coEvery { tokenRepo.getSubId() } returns "54321"

        runTest {
            assertTrue(authRepo.isDifferentUser())

            coVerify(exactly = 1) {
                tokenRepo.getSubId()
                cryptoProvider.decrypt("54321")
                cryptoProvider.encrypt("".toByteArray(StandardCharsets.UTF_8))
            }

            coVerify(exactly = 1) {
                tokenRepo.saveSubId("54321")
            }
        }
    }

    @Test
    fun `Given a user is logging in again and has no sub id stored in shared prefs but the data repo has no sub id, when isDifferentUser() called, then returns false`() {
        every {
            cryptoProvider.encrypt(any())
        } returns Result.success("54321")
        coEvery { tokenRepo.getSubId() } returns null
        runTest {
            assertFalse(authRepo.isDifferentUser())
            coVerify(exactly = 1) {
                cryptoProvider.encrypt("".toByteArray(StandardCharsets.UTF_8))
                tokenRepo.saveSubId("54321")
                tokenRepo.getSubId()
            }
        }
    }

    @Test
    fun `Given a user is logging in again and has no sub id stored in shared prefs but encryption fails, when isDifferentUser() called, then returns true`() {
        every {
            cryptoProvider.encrypt(any())
        } returns Result.failure(Throwable())
        every {
            cryptoProvider.decrypt("54321")
        } returns Result.success("12345".toByteArray(StandardCharsets.UTF_8))
        coEvery { tokenRepo.getSubId() } returns "54321"
        runTest {
            assertTrue(authRepo.isDifferentUser())
            coVerify(exactly = 1) {
                cryptoProvider.decrypt("54321")
                cryptoProvider.encrypt("".toByteArray(StandardCharsets.UTF_8))
                tokenRepo.getSubId()
            }
        }
    }

    @Test
    fun `Given a user is logging in again and has no sub id stored in shared prefs but decryption fails, when isDifferentUser() called, then returns false`() {
        every {
            cryptoProvider.encrypt(any())
        } returns Result.success("54321")
        every {
            cryptoProvider.decrypt("54321")
        } returns Result.failure(Throwable())
        coEvery { tokenRepo.getSubId() } returns "54321"
        runTest {
            assertFalse(authRepo.isDifferentUser())
            coVerify(exactly = 1) {
                cryptoProvider.decrypt("54321")
                cryptoProvider.encrypt("".toByteArray(StandardCharsets.UTF_8))
                tokenRepo.saveSubId("54321")
                tokenRepo.getSubId()
            }
        }
    }

    @Test
    fun `Given a user is logging in again and has no sub id stored in shared prefs, when isDifferentUser() called, then returns true`() {
        every {
            cryptoProvider.encrypt(any())
        } returns Result.success("54321")
        every {
            cryptoProvider.decrypt("54321")
        } returns Result.success("12345".toByteArray(StandardCharsets.UTF_8))
        coEvery { tokenRepo.getSubId() } returns "54321"
        runTest {
            assertTrue(authRepo.isDifferentUser())
            coVerify(exactly = 1) {
                cryptoProvider.decrypt("54321")
                cryptoProvider.encrypt("".toByteArray(StandardCharsets.UTF_8))
                tokenRepo.saveSubId("54321")
                tokenRepo.getSubId()
            }
        }
    }
}
