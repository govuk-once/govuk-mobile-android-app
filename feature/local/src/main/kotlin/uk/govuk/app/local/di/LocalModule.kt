package uk.govuk.app.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.local.RoomEncryptionHelper
import uk.govuk.app.local.BuildConfig
import uk.govuk.app.local.DefaultLocalFeature
import uk.govuk.app.local.LocalFeature
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.local.LocalDao
import uk.govuk.app.local.data.local.LocalDatabase
import uk.govuk.app.local.data.remote.LocalApi

@InstallIn(SingletonComponent::class)
@Module
internal class LocalModule {

    @Provides
    @Singleton
    fun providesLocalDatabase(
        @ApplicationContext context: Context,
        roomEncryptionHelper: RoomEncryptionHelper,
        analyticsClient: AnalyticsClient
    ): LocalDatabase {
        val key = roomEncryptionHelper.getKey()
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(key)
        return Room.databaseBuilder(context, LocalDatabase::class.java, "local.db")
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    @Singleton
    fun providesLocalDao(
        database: LocalDatabase
    ): LocalDao = database.localDao()

    @Provides
    @Singleton
    fun providesLocalFeature(
        localRepo: LocalRepo
    ): LocalFeature = DefaultLocalFeature(localRepo)

    @Provides
    @Singleton
    fun providesLocalApi(): LocalApi = Retrofit.Builder()
        .baseUrl(BuildConfig.LOCAL_SERVICES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LocalApi::class.java)
}
