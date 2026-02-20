package uk.gov.govuk.config.data.remote.source

import com.google.gson.Gson
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Error
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GovUkConfigDataSource @Inject constructor(
    private val configApi: ConfigApi,
    private val gson: Gson,
    private val signatureValidator: SignatureValidator
) {
    suspend fun fetchConfig(): Result<Config> {
        return try {
            val response = configApi.getConfig()
            if (response.isSuccessful) {
                response.body()?.let {
                    val signature = response.headers()["x-amz-meta-govuk-sig"] ?: ""
                    val valid = signatureValidator.isValidSignature(signature, it)
                    if (!valid) {
                        return InvalidSignature()
                    }

                    val configResponse = gson.fromJson(it, ConfigResponse::class.java)
                    Success(configResponse.config)
                } ?: Error()
            } else {
                Error()
            }
        } catch (_: UnknownHostException) {
            DeviceOffline()
        } catch (_: Exception) {
            Error()
        }
    }
}