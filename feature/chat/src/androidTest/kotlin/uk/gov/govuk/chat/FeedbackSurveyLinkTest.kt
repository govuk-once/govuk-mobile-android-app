package uk.gov.govuk.chat

import androidx.annotation.StringRes
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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
 * Guards against the survey link reappearing after navigating away and back.
 *
 * When a conversation is reloaded from the API, feedbackReaction is restored but the
 * Compose-local surveyLinkClicked state resets. Without the fix, the UI shows the survey
 * link again instead of "Thank you".
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class FeedbackSurveyLinkTest {
    private val positiveLinkText = getString(R.string.chat_feedback_positive_link_text)
    private val negativeLinkText = getString(R.string.chat_feedback_negative_link_text)
    private val thankYouText = getString(R.string.chat_feedback_thank_you_text)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun positive_feedback_already_given_shows_thank_you_not_survey_link() {
        setupScreenWithExistingFeedback(reaction = "positive")

        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))
        composeTestRule.onNodeWithText(thankYouText).assertIsDisplayed()
        composeTestRule.onNodeWithText(positiveLinkText).assertDoesNotExist()
    }

    @Test
    fun negative_feedback_already_given_shows_thank_you_not_survey_link() {
        setupScreenWithExistingFeedback(reaction = "negative")

        composeTestRule.waitUntilExactlyOneExists(hasText(thankYouText))
        composeTestRule.onNodeWithText(thankYouText).assertIsDisplayed()
        composeTestRule.onNodeWithText(negativeLinkText).assertDoesNotExist()
    }

    private fun setupScreenWithExistingFeedback(reaction: String) {
        val chatEntry = ChatEntry(
            id = "questionId",
            question = "How do I renew my passport?",
            answer = "You can renew your passport online.",
            sources = null,
            shouldAnimate = false,
            conversationId = "conversationId",
            answerId = "answerId",
            feedbackReaction = reaction
        )

        composeTestRule.setContent {
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
                    isAnalyticsEnabled = true,
                    isImeVisible = false,
                    uiEvents = UiEvents(
                        onQuestionUpdated = { _ -> },
                        onSubmit = { _ -> },
                        onClear = { },
                        onFeedbackClick = { _, _, _ -> },
                        onFeedbackGiven = { _, _, _ -> }
                    )
                )
            }
        }
    }

    private fun getString(@StringRes id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }
}
