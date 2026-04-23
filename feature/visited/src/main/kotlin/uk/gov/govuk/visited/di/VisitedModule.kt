package uk.gov.govuk.visited.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.data.local.RoomEncryptionHelper
import uk.gov.govuk.visited.Visited
import uk.gov.govuk.visited.VisitedClient
import uk.gov.govuk.visited.data.store.VisitedDao
import uk.gov.govuk.visited.data.store.VisitedDatabase
import uk.gov.govuk.visited.data.store.VisitedMigrationCallback
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class VisitedModule {

    @Provides
    @Singleton
    fun providesVisitedDatabase(
        @ApplicationContext context: Context,
        roomEncryptionHelper: RoomEncryptionHelper,
        realmEncryptionHelper: RealmEncryptionHelper,
        analyticsClient: AnalyticsClient
    ): VisitedDatabase {
        val key = roomEncryptionHelper.getKey()
        return Room.databaseBuilder(context, VisitedDatabase::class.java, "visited.db")
            .openHelperFactory(SupportFactory(key, null, false))
            .addCallback(VisitedMigrationCallback(realmEncryptionHelper, analyticsClient))
            .build()
    }

    @Provides
    @Singleton
    fun providesVisitedDao(database: VisitedDatabase): VisitedDao = database.visitedDao()

    @Provides
    @Singleton
    fun provideVisited(visitedClient: VisitedClient): Visited = visitedClient
}
