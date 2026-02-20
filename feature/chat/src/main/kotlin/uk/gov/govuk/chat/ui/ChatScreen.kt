package uk.gov.govuk.chat.ui

import android.content.Context
import android.content.res.Configuration
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.chat.ui.component.ChatEntry
import uk.gov.govuk.chat.ui.component.ChatInput
import uk.gov.govuk.chat.ui.component.IntroMessages
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.component.InfoAlert
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

internal class AnalyticsEvents(
    val onPageView: (String, String, String) -> Unit,
    val onNavigationActionItemClicked: (String, String) -> Unit,
    val onFunctionActionItemClicked: (String, String, String) -> Unit,
    val onQuestionSubmit: () -> Unit,
    val onMarkdownLinkClicked: (String, String) -> Unit,
    val onSourcesExpanded: () -> Unit
)

internal class UiEvents(
    val onQuestionUpdated: (String) -> Unit,
    val onSubmit: (String) -> Unit,
    val onClear: () -> Unit,
)

@Composable
internal fun ChatRoute(
    onShowOnboarding: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    onAuthError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val onPageView: (String, String, String) -> Unit = { klass, name, title ->
        viewModel.onPageView(
            screenClass = klass,
            screenName = name,
            title = title
        )
    }

    uiState?.let {
        when (it) {
            ChatUiState.Onboarding -> onShowOnboarding()
            is ChatUiState.Error -> {
                ErrorScreen(
                    canRetry = it.canRetry,
                    onPageView = onPageView,
                    onRetry = { viewModel.clearConversation() },
                    modifier = modifier
                )
            }
            is ChatUiState.Default -> {
                ChatScreen(
                    uiState = it,
                    analyticsEvents = AnalyticsEvents(
                        onPageView = onPageView,
                        onNavigationActionItemClicked = { text, url ->
                            viewModel.onNavigationActionItemClicked(text, url)
                        },
                        onFunctionActionItemClicked = { text, section, action ->
                            viewModel.onFunctionActionItemClicked(text, section, action)
                        },
                        onQuestionSubmit = { viewModel.onQuestionSubmit() },
                        onMarkdownLinkClicked = { text, url -> viewModel.onMarkdownLinkClicked(text, url) },
                        onSourcesExpanded = { viewModel.onSourcesExpanded() }
                    ),
                    launchBrowser = launchBrowser,
                    hasConversation = it.chatEntries.isNotEmpty(),
                    uiEvents = UiEvents(
                        onQuestionUpdated = { question ->
                            viewModel.onQuestionUpdated(question)
                        },
                        onSubmit = { question ->
                            viewModel.onSubmit(question)
                        },
                        onClear = {
                            viewModel.clearConversation()
                        }
                    ),
                    chatUrls = viewModel.chatUrls,
                    modifier = modifier
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.authError.collect {
            onAuthError()
        }
    }
}

@Composable
internal fun ChatScreen(
    uiState: ChatUiState.Default,
    launchBrowser: (url: String) -> Unit,
    hasConversation: Boolean,
    uiEvents: UiEvents,
    analyticsEvents: AnalyticsEvents,
    chatUrls: ChatUrls,
    modifier: Modifier = Modifier,
    isTalkBackActive: Boolean = isTalkBackEnabled()
) {
    val listState = rememberLazyListState()
    val chatEntries = uiState.chatEntries.toList()
    val animationDuration = 500
    val coroutineScope = rememberCoroutineScope()
    var showPiiErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isPiiError) {
        if (uiState.isPiiError) {
            showPiiErrorDialog = true
        }
    }

    LaunchedEffect(Unit) {
        analyticsEvents.onPageView(
            Analytics.CHAT_SCREEN_CLASS,
            Analytics.CHAT_SCREEN_NAME,
            Analytics.CHAT_SCREEN_TITLE
        )
    }

    Box(
        modifier.fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.chatBackground)
            .padding(top = GovUkTheme.spacing.medium)
    ) {
        Column(
            Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = GovUkTheme.spacing.medium)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Title2BoldLabel(
                            text = stringResource(R.string.bot_header_text),
                            modifier = Modifier
                                .padding(vertical = GovUkTheme.spacing.medium)
                                .weight(1f)
                                .semantics { heading() },
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    IntroMessages(chatEntries.isEmpty()) // only animate if no conversation
                }

                items(chatEntries) {
                    ChatEntry(
                        chatEntry = it.second,
                        onMarkdownLinkClicked = { text, url ->
                            launchBrowser(url)
                            analyticsEvents.onMarkdownLinkClicked(text, url)
                        },
                        animationDuration = animationDuration,
                        onSourcesExpanded = {
                            analyticsEvents.onSourcesExpanded()
                            coroutineScope.launch {
                                delay(150)
                                listState.animateScrollBy(128f)
                            }
                        },
                    )
                }

                item {
                    Spacer(Modifier.height(20.dp))
                }
            }

            Column {
                    ChatInput(
                        uiState,
                        hasConversation = hasConversation,
                        onNavigationActionItemClicked = { text, url ->
                            launchBrowser(url)
                            analyticsEvents.onNavigationActionItemClicked(text, url)
                        },
                        onFunctionActionItemClicked = { text, section, action ->
                            analyticsEvents.onFunctionActionItemClicked(text, section, action)
                        },
                        onClear = uiEvents.onClear,
                        onQuestionUpdated = uiEvents.onQuestionUpdated,
                        onSubmit = { question ->
                            uiEvents.onSubmit(question)
                            analyticsEvents.onQuestionSubmit()
                        },
                        chatUrls = chatUrls,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = GovUkTheme.spacing.medium)
                            .padding(
                                top = GovUkTheme.spacing.small,
                                bottom = GovUkTheme.spacing.medium
                            ),
                        isTalkBackActive = isTalkBackActive
                    )
            }
        }
    }

    if (showPiiErrorDialog) {
        InfoAlert(
            title = R.string.pii_error_title,
            message = R.string.pii_error_message,
            buttonText = R.string.pii_error_ok_button,
            onDismiss = { showPiiErrorDialog = false }
        )
    }

    if (chatEntries.isNotEmpty()) {
        val answer = chatEntries.last().second.answer
        LaunchedEffect(answer) {
            if (answer.isEmpty()) {
                // If the updated entry is the user's question then immediately scroll to the bottom
                // wait for the loading text to fade in and then scroll to the bottom again if required
                listState.animateScrollToItem(chatEntries.size + 1) // + 1 due to header and welcome message
                delay(animationDuration.toLong() + 100)
                listState.animateScrollToItem(chatEntries.size + 1) // + 1 due to header and welcome message
            } else {
                // If the updated entry is the answer then wait for the answer to fade in and scroll to
                // the entry
                delay(animationDuration.toLong() + 100)
                listState.animateScrollToItem(chatEntries.size + 1) // + 1 due to header and welcome message
            }
        }
    }
}

@Composable
private fun isTalkBackEnabled(): Boolean {
    val context = LocalContext.current
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

    var isEnabled by remember {
        mutableStateOf(accessibilityManager.isTouchExplorationEnabled)
    }

    DisposableEffect(accessibilityManager) {
        val listener = AccessibilityManager.TouchExplorationStateChangeListener { enabled ->
            isEnabled = enabled
        }
        accessibilityManager.addTouchExplorationStateChangeListener(listener)

        onDispose {
            accessibilityManager.removeTouchExplorationStateChangeListener(listener)
        }
    }

    return isEnabled
}

private fun analyticsEvents() = AnalyticsEvents(
    onPageView = { _, _, _ -> },
    onFunctionActionItemClicked = { _, _, _ -> },
    onNavigationActionItemClicked = { _, _ ->  },
    onQuestionSubmit = { },
    onMarkdownLinkClicked = { _, _ -> },
    onSourcesExpanded = { }
)

private fun clickEvents() = UiEvents(
    onQuestionUpdated = { _ -> },
    onSubmit = { _ -> },
    onClear = { }
)

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModeChatScreenPreview() {
    GovUkTheme {
        ChatScreen(
            uiState = ChatUiState.Default(isLoading = false),
            analyticsEvents = analyticsEvents(),
            launchBrowser = { _ -> },
            hasConversation = false,
            chatUrls = ChatUrls("", "", "", ""),
            uiEvents = clickEvents()
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModeChatScreenPreview() {
    GovUkTheme {
        ChatScreen(
            uiState = ChatUiState.Default(isLoading = false),
            analyticsEvents = analyticsEvents(),
            launchBrowser = { _ -> },
            hasConversation = false,
            chatUrls = ChatUrls("", "", "", ""),
            uiEvents = clickEvents()
        )
    }
}
