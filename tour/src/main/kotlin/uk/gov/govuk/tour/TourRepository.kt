package uk.gov.govuk.tour

import kotlinx.coroutines.flow.Flow

interface TourRepository {
    fun isTourSeen(tourId: String): Flow<Boolean>
    suspend fun markTourSeen(tourId: String)
}
