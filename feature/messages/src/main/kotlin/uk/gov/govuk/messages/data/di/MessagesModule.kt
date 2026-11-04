package uk.gov.govuk.messages.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import uk.gov.govuk.messages.DefaultMessagesFeature
import uk.gov.govuk.messages.MessagesFeature
import uk.gov.govuk.messages.data.DateProvider
import uk.gov.govuk.messages.data.DateProviderImpl
import uk.gov.govuk.messages.data.MessagesRepo
import uk.gov.govuk.messages.data.MessagesRepoImpl
import uk.gov.govuk.messages.data.remote.NotificationsApi

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class MessagesScope

@InstallIn(SingletonComponent::class)
@Module
internal object MessagesModule {
    @Provides
    @Singleton
    fun providesMessagesApi(@Named("FlexRetrofit") retrofit: Retrofit): NotificationsApi = retrofit.create(NotificationsApi::class.java)

    @Provides
    @Singleton
    fun provideMessagesRepo(messagesRepo: MessagesRepoImpl): MessagesRepo = messagesRepo

    @Provides
    @Singleton
    fun providesMessagesFeature(messagesRepo: MessagesRepo): MessagesFeature = DefaultMessagesFeature(messagesRepo)

    @Provides
    @Singleton
    fun provideDateProvider(): DateProvider = DateProviderImpl()

    @Provides
    @Singleton
    @MessagesScope
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
