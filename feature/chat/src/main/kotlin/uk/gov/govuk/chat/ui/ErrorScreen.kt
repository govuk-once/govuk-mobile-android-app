package uk.gov.govuk.chat.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.chat.ui.component.ChatErrorPageNoRetry
import uk.gov.govuk.chat.ui.component.ChatErrorPageWithRetry

@Composable
internal fun ErrorScreen(
    canRetry: Boolean,
    onPageView: (String, String, String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (canRetry) {
        ErrorScreenWithRetry(
            onPageView = onPageView,
            onRetry = onRetry,
            modifier
        )
    } else {
        ErrorScreenNoRetry(onPageView, modifier)
    }
}

@Composable
private fun ErrorScreenNoRetry(
    onPageView: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasTrackedPageView by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasTrackedPageView) {
            onPageView(
                Analytics.CHAT_ERROR_SCREEN_CLASS,
                Analytics.CHAT_ERROR_SCREEN_NAME,
                Analytics.CHAT_ERROR_SCREEN_TITLE,
            )
            hasTrackedPageView = true
        }
    }

    ChatErrorPageNoRetry(
        modifier
            .windowInsetsPadding(WindowInsets.statusBars)
    )
}

@Composable
private fun ErrorScreenWithRetry(
    onPageView: (String, String, String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasTrackedPageView by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasTrackedPageView) {
            onPageView(
                Analytics.CHAT_ERROR_SCREEN_CLASS,
                Analytics.CHAT_ERROR_RETRY_SCREEN_NAME,
                Analytics.CHAT_ERROR_RETRY_SCREEN_TITLE,
            )
            hasTrackedPageView = true
        }
    }

    ChatErrorPageWithRetry(
        onRetry = onRetry,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
    )
}