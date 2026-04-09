package uk.gov.govuk.tour

import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.tour.data.local.TourDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultTourRepository @Inject constructor(
    private val dataStore: TourDataStore
) : TourRepository {

    override fun isTourSeen(tourId: String): Flow<Boolean> =
        dataStore.isTourSeen(tourId)

    override suspend fun markTourSeen(tourId: String) =
        dataStore.markTourSeen(tourId)
}
