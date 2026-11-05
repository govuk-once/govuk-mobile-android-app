package uk.gov.govuk.settings.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.govuk.settings.data.LinkedAccountsRepoImpl
import uk.gov.govuk.settings.domain.LinkedAccountsRepo

@Module
@InstallIn(SingletonComponent::class)
internal object LinkedAccountsModule {

    @Provides
    fun provideLinkedAccountsRepo(
        repo: LinkedAccountsRepoImpl
    ): LinkedAccountsRepo {
        return repo
    }
}