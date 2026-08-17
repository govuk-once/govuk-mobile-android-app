package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A launched effect that only ever runs once on initial composition
 */
@Composable
fun RunOnceLaunchedEffect(action: () -> Unit) {
    var hasRunOnce by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasRunOnce) {
            action()
            hasRunOnce = true
        }
    }
}
