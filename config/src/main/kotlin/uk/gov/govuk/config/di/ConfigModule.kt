package uk.gov.govuk.config.di

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import uk.gov.govuk.config.BuildConfig
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.ConfigRepoImpl
import uk.gov.govuk.config.data.flags.DebugFlags
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.ContentApi
import uk.gov.govuk.config.data.serialisation.EmergencyBannerTypeAdapter
import javax.inject.Singleton

internal class FakeConfigInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val body = """
{
  "platform": "Android",
  "config": {
    "available": true,
    "minimumVersion": "0.0.1",
    "recommendedVersion": "0.0.1",
    "searchApiUrl": "https://search.publishing.service.gov.uk/v0_1/search.json",
    "refreshTokenExpirySeconds": 604800,
    "chatPollIntervalSeconds": 3,
    "chatUrls": {
      "termsAndConditions": "https://www.gov.uk/guidance/govuk-chat-terms-and-conditions",
      "privacyNotice": "https://www.gov.uk/government/publications/govuk-chat-privacy-notice",
      "about": "https://www.gov.uk/guidance/about-govuk-chat",
      "feedback": "https://www.gov.uk/contact/govuk-app/leave-feedback-about-govuk-chat"
    },
    "termsAndConditions": {
      "lastUpdated": "2025-03-18T00:00:00Z",
      "url": "https://www.gov.uk/guidance/govuk-app-terms-and-conditions",
      "contentItemApiUrl": "https://www.gov.uk/api/content/guidance/govuk-app-terms-and-conditions"
    },
    "chatBanner_v2": {
      "id": "govuk_chat_banner_01_2026",
      "title": "Introducing GOV.UK Chat",
      "body": "An experimental AI tool for finding quick answers",
      "link": {
        "title": "Ask a question",
        "url": "govuk://gov.uk/chat"
      }
    },
    "dvlaUrls": {
      "addVehicle": "https://driver-and-vehicles-account.service.gov.uk/add_vehicle",
      "renewLicence": "https://www.gov.uk/renew-driving-licence",
      "soldVehicle": "https://www.gov.uk/sold-bought-vehicle",
      "sornRules": "https://www.gov.uk/sorn-statutory-off-road-notification",
      "makeSorn": "https://www.gov.uk/make-a-sorn",
      "getLogbook": "https://www.gov.uk/vehicle-log-book",
      "changeLogbookAddress": "https://www.gov.uk/change-address-v5c",
      "cancelTax": "https://www.gov.uk/vehicle-tax-refund",
      "changeLicenceAddress": "https://www.gov.uk/change-address-driving-licence",
      "changeNameGenderLicence": "https://www.gov.uk/change-name-driving-licence",
      "replaceLicence": "https://www.gov.uk/replace-a-driving-licence",
      "taxVehicle": "https://www.gov.uk/vehicle-tax",
      "manageTaxPayment": "https://www.gov.uk/vehicle-tax-direct-debit/renewing",
      "historicVehicles": "https://www.gov.uk/historic-vehicles",
      "checkMot": "https://www.check-mot.service.gov.uk/results?registration=[NUMBER PLATE]&checkRecalls=true",
      "driverDetails": "https://driver-and-vehicles-account.service.gov.uk/driver_details",
      "account": "https://driver-and-vehicles-account.service.gov.uk",
      "drivingRecord": "https://driver-and-vehicles-account.service.gov.uk/driver_details?locale=en#Entitlements"
    },
    "releaseFlags": {
      "chat": true,
      "localServices": true,
      "notifications": true,
      "onboarding": true,
      "recentActivity": true,
      "search": true,
      "topics": true,
      "profile_v2": true
    },
    "promoBanners": [
      {
        "id": "govuk_promo_banner_07_2026",
        "title": "Going abroad?",
        "body": "Check the advice for your destination before you go",
        "link": {
          "title": "Check travel advice",
          "url": "govuk://gov.uk/web?url=https://www.gov.uk/foreign-travel-advice"
        },
        "image": "going_abroad",
        "type": "external"
      },
      {
        "id": "govuk_promo_banner_07_2027",
        "title": "Same time next year?",
        "body": "Check the advice for your destination before you go",
        "link": {
          "title": "Check travel advice",
          "url": "govuk://gov.uk/web?url=https://www.gov.uk/foreign-travel-advice"
        },
        "image": "going_abroad",
        "type": "external"
      },
      {
        "id": "govuk_promo_banner_08_2026",
        "title": "Going abroad again?",
        "body": "Check the advice for your destination before you go",
        "link": {
          "title": "Check travel advice",
          "url": "govuk://gov.uk/web?url=https://www.gov.uk/foreign-travel-advice"
        },
        "image": "going_abroad",
        "type": "external"
      }
    ],
    "version": "0.0.76",
    "lastUpdated": "2026-07-09T10:45:28.096Z"
  },
  "signature": "fake-signature"
}
        """.trimIndent()

        if (request.url.encodedPath.contains("appinfo/android")) {
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("OK")
                .header("x-amz-meta-govuk-sig", "fake-signature")
                .body(
                    body.toResponseBody("application/json".toMediaTypeOrNull())
                )
                .build()
        }
        return chain.proceed(request)
    }
}

@InstallIn(SingletonComponent::class)
@Module
class ConfigModule {

    @Provides
    @Singleton
    fun provideConfigRepo(configRepo: ConfigRepoImpl): ConfigRepo {
        return configRepo
    }

    @Provides
    @Singleton
    fun providesConfigApi(gson: Gson): ConfigApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(FakeConfigInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.CONFIG_BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ConfigApi::class.java)
    }

    @Provides
    @Singleton
    fun providesContentApi(gson: Gson): ContentApi {
        return Retrofit.Builder()
            .baseUrl("https://www.gov.uk/") // placeholder URL required by Retrofit
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ContentApi::class.java)
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                EmergencyBannerTypeAdapter::class.java,
                EmergencyBannerTypeAdapter()
            )
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun providesFlagRepo(
        debugFlags: DebugFlags,
        configRepo: ConfigRepo
    ): FlagRepo {
        return FlagRepo(
            debugEnabled = BuildConfig.DEBUG,
            debugFlags = debugFlags,
            configRepo = configRepo
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig

}
