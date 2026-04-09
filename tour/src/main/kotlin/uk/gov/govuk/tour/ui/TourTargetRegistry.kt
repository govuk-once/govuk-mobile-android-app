package uk.gov.govuk.tour.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Rect

class TourTargetRegistry {
    val bounds: SnapshotStateMap<String, Rect> = mutableStateMapOf()

    fun register(key: String, rect: Rect) {
        bounds[key] = rect
    }
}

val LocalTourTargetRegistry = compositionLocalOf<TourTargetRegistry?> { null }
