package uk.gov.govuk.design.ui.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.govuk.design.ui.util.StringProvider
import uk.gov.govuk.design.ui.util.StringProviderImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DesignModule {

    @Provides
    @Singleton
    fun provideStringProvider(
        @ApplicationContext context: Context
    ): StringProvider {
        return StringProviderImpl(context)
    }
}