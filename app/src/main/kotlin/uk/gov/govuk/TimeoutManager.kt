package uk.gov.govuk

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TimeoutManager @Inject constructor(

) {
    private var job: Job? = null
    private var lastInteractionTime: Long = 0L

    fun onUserInteraction(
        interactionTime: Long = SystemClock.elapsedRealtime(),
        warningInterval: Long = 13 * 60 * 1000L, // 13 mins
        timeoutInterval: Long = 15 * 60 * 1000L, // 15 mins
        onWarning: () -> Unit,
        onTimeout: () -> Unit,
    ) {
        if (interactionTime - lastInteractionTime >= 1000) {
            lastInteractionTime = interactionTime
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch {
                delay(warningInterval)
                onWarning()
                delay(timeoutInterval - warningInterval)
                onTimeout()
            }
        }
    }
}