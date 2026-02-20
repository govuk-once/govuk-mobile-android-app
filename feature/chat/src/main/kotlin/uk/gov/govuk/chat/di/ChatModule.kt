package uk.gov.govuk.chat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.ChatFeature
import uk.gov.govuk.chat.DefaultChatFeature
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.remote.AuthorizationInterceptor
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

internal class HeaderInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()
        currentRequest.addHeader("Content-Type", "application/json")

        val newRequest = currentRequest.build()
        return chain.proceed(newRequest)
    }
}

@InstallIn(SingletonComponent::class)
@Module
internal class ChatModule {

    @Provides
    @Singleton
    fun providesChatFeature(chatRepo: ChatRepo): ChatFeature {
        return DefaultChatFeature(chatRepo)
    }

    @Provides
    @Singleton
    fun providesChatApi(authRepo: AuthRepo): ChatApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(AuthorizationInterceptor(authRepo))
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.CHAT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ChatApi::class.java)
    }

    @Singleton
    @Provides
    @Named("chat_prefs")
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile("chat_preferences") }
        )
    }
}
