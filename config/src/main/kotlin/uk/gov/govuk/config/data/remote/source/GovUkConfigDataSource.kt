package uk.gov.govuk.config.data.remote.source

import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.ContentApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.config.data.remote.model.TermsAndConditionsTimestamp
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
    private val contentApi: ContentApi,
    private val gson: Gson,
    private val signatureValidator: SignatureValidator
) {
    suspend fun fetchConfig(): Result<Config> = coroutineScope {
        return@coroutineScope try {
            val configDeferred = async { configApi.getConfig() }
            val termsDeferred = async { contentApi.getContent() }

            val response = configDeferred.await()
            val content = termsDeferred.await()

            if (response.isSuccessful) {
                response.body()?.let {
                    val signature = response.headers()["x-amz-meta-govuk-sig"] ?: ""
                    val valid = signatureValidator.isValidSignature(signature, it)
                    if (!valid) {
                        return@coroutineScope InvalidSignature()
                    }

                    val configResponse = gson.fromJson(it, ConfigResponse::class.java)
                    val config = configResponse.config

                    if (content.isSuccessful) {
                        val contentItemTimestamp: String = content.body().run {
                            gson.fromJson(this, TermsAndConditionsTimestamp::class.java).publicUpdatedAt
                        }

                        config.termsAndConditions = config.termsAndConditions?.copy(
                            lastUpdated = contentItemTimestamp
                        )

                        Success(config)
                    } else {
                        Error() // Can't get the content item
                    }
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
