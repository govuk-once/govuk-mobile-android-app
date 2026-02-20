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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import uk.gov.govuk.config.BuildConfig
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.ConfigRepoImpl
import uk.gov.govuk.config.data.flags.DebugFlags
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.serialisation.EmergencyBannerTypeAdapter
import javax.inject.Singleton

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
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CONFIG_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ConfigApi::class.java)
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                EmergencyBannerTypeAdapter::class.java,
                EmergencyBannerTypeAdapter()
            )
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