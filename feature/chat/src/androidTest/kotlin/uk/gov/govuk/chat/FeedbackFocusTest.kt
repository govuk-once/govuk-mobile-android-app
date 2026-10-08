package uk.gov.govuk.chat

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.chat.ui.AnalyticsEvents
import uk.gov.govuk.chat.ui.ChatScreen
import uk.gov.govuk.chat.ui.UiEvents
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.theme.GovUkTheme

/**
 * When the user taps a feedback icon or link, the tapped element is replaced. Unless focus is
 * moved to its replacement, TalkBack falls back to the header (which holds focus from screen
 * load) and reads that instead.
 * These tests guard against focus not following the user's feedback.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class FeedbackFocusTest {
    private val positiveIconText = getString(R.string.chat_feedback_positive_icon_text)
    private val negativeIconText = getString(R.string.chat_feedback_negative_icon_text)
    private val positiveLinkText = getString(R.string.chat_feedback_positive_link_text)
    private val negativeLinkText = getString(R.string.chat_feedback_negative_link_text)
    private val thankYouText = getString(R.string.chat_feedback_thank_you_text)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tapping_thumbs_up_with_analytics_enabled_moves_focus_to_the_positive_link() {
        setupChatScreen(isAnalyticsEnabled = true)

        tapIcon(positiveIconText)

        composeTestRule.waitUntilExactlyOneExists(hasText(positiveLinkText))
        composeTestRule.onNodeWithText(positiveLinkText).assertIsFocused()
    }

    @Test
    fun tapping_thumbs_down_with_analytics_enabled_moves_focus_to_the_negative_link() {
        setupChatScreen(isAnalyticsEnabled = true)

        tapIcon(negativeIconText)

        composeTestRule.waitUntilExactlyOneExists(hasText(negativeLinkText))
        composeTestRule.onNodeWithText(negativeLinkText).assertIsFocused()
    }

    @Test
    fun tapping_thumbs_up_with_analytics_disabled_moves_focus_to_the_thank_you_text() {
        setupChatScreen(isAnalyticsEnabled = false)

        tapIcon(positiveIconText)

        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))
        composeTestRule.onNodeWithText(thankYouText).assertIsFocused()
    }

    @Test
    fun tapping_thumbs_down_with_analytics_disabled_moves_focus_to_the_thank_you_text() {
        setupChatScreen(isAnalyticsEnabled = false)

        tapIcon(negativeIconText)

        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))
        composeTestRule.onNodeWithText(thankYouText).assertIsFocused()
    }

    @Test
    fun tapping_the_positive_link_moves_focus_to_the_thank_you_text() {
        setupChatScreen(isAnalyticsEnabled = true)
        tapIcon(positiveIconText)
        composeTestRule.waitUntilExactlyOneExists(hasText(positiveLinkText))

        composeTestRule.onNodeWithText(positiveLinkText).performClick()

        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))
        composeTestRule.onNodeWithText(thankYouText).assertIsFocused()
    }

    @Test
    fun tapping_the_negative_link_moves_focus_to_the_thank_you_text() {
        setupChatScreen(isAnalyticsEnabled = true)
        tapIcon(negativeIconText)
        composeTestRule.waitUntilExactlyOneExists(hasText(negativeLinkText))

        composeTestRule.onNodeWithText(negativeLinkText).performClick()

        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))
        composeTestRule.onNodeWithText(thankYouText).assertIsFocused()
    }

    @Test
    fun focus_is_not_moved_to_the_thank_you_text_when_state_is_restored() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent { ChatScreenContent(isAnalyticsEnabled = false) }
        tapIcon(positiveIconText)
        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))

        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))
        composeTestRule.onNodeWithText(thankYouText).assertIsNotFocused()
    }

    private fun tapIcon(contentDescription: String) {
        composeTestRule.waitUntilExactlyOneExists(hasContentDescription(contentDescription))
        composeTestRule.onNodeWithContentDescription(contentDescription).performClick()
    }

    private fun setupChatScreen(isAnalyticsEnabled: Boolean) {
        composeTestRule.setContent { ChatScreenContent(isAnalyticsEnabled) }
    }

    @Composable
    private fun ChatScreenContent(isAnalyticsEnabled: Boolean) {
        // Already answered and not animating, so the feedback icons are shown straight away
        val chatEntry = ChatEntry(
            id = "questionId",
            question = "How do I renew my passport?",
            answer = "You can renew your passport online.",
            sources = null,
            shouldAnimate = false
        )

        GovUkTheme {
            ChatScreen(
                uiState = ChatUiState.Default(
                    chatEntries = linkedMapOf(chatEntry.id to chatEntry)
                ),
                analyticsEvents = AnalyticsEvents(
                    onPageView = { _, _, _ -> },
                    onNavigationActionItemClicked = { _, _ -> },
                    onFunctionActionItemClicked = { _, _, _ -> },
                    onQuestionSubmit = { },
                    onMarkdownLinkClicked = { _, _ -> },
                    onSourcesExpanded = { },
                    onExampleQuestionsViewed = { },
                    onExampleQuestionSelected = { _, _ -> }
                ),
                launchBrowser = { _ -> },
                hasConversation = true,
                chatUrls = ChatUrls("", "", "", ""),
                chatExampleQuestions = emptyList(),
                isAnalyticsEnabled = isAnalyticsEnabled,
                isImeVisible = false,
                uiEvents = UiEvents(
                    onQuestionUpdated = { _ -> },
                    onSubmit = { _ -> },
                    onClear = { },
                    onFeedbackClick = { _, _, _ -> }
                )
            )
        }
    }

    private fun getString(@StringRes id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }
}
