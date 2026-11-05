package uk.gov.govuk.visited.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.govuk.visited.Visited
import uk.gov.govuk.visited.VisitedClient
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class VisitedModule {
    @Provides
    @Singleton
    fun provideVisited(visitedClient: VisitedClient): Visited = visitedClient
}
