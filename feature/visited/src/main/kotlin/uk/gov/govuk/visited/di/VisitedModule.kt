package uk.gov.govuk.visited.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.govuk.visited.Visited
import uk.gov.govuk.visited.VisitedClient

@InstallIn(SingletonComponent::class)
@Module
internal class VisitedModule {
    @Provides
    @Singleton
    fun provideVisited(visitedClient: VisitedClient): Visited = visitedClient
}
