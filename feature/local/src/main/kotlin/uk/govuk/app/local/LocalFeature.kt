package uk.govuk.app.local

import kotlinx.coroutines.flow.Flow

interface LocalFeature {

    fun hasLocalAuthority(): Flow<Boolean>

    suspend fun clear()

}