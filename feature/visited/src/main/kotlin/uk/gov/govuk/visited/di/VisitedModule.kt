package uk.gov.govuk.visited.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.local.RoomEncryptionHelper
import uk.gov.govuk.visited.Visited
import uk.gov.govuk.visited.VisitedClient
import uk.gov.govuk.visited.data.store.VisitedDao
import uk.gov.govuk.visited.data.store.VisitedDatabase

@InstallIn(SingletonComponent::class)
@Module
internal class VisitedModule {

    @Provides
    @Singleton
    fun providesVisitedDatabase(
        @ApplicationContext context: Context,
        roomEncryptionHelper: RoomEncryptionHelper,
        analyticsClient: AnalyticsClient
    ): VisitedDatabase {
        val key = roomEncryptionHelper.getKey()
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(key)
        return Room.databaseBuilder(context, VisitedDatabase::class.java, "visited.db")
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    @Singleton
    fun providesVisitedDao(
        database: VisitedDatabase
    ): VisitedDao = database.visitedDao()

    @Provides
    @Singleton
    fun provideVisited(
        visitedClient: VisitedClient
    ): Visited = visitedClient
}
