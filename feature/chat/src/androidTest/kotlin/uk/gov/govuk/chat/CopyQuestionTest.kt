package uk.gov.govuk.chat

import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.chat.ui.AnalyticsEvents
import uk.gov.govuk.chat.ui.ChatScreen
import uk.gov.govuk.chat.ui.UiEvents
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.theme.GovUkTheme

@RunWith(AndroidJUnit4::class)
class CopyQuestionTest {
    private val question = "How do I renew my passport? It's £88.50 – \"urgent\" 🙏"
    private val questionAltText = "${getString(R.string.question_alt)} $question"
    private val copyText = getString(R.string.copy_text)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun long_pressing_a_sent_question_shows_the_copy_option() {
        setupChatScreen()

        longPressQuestion()

        composeTestRule.onNodeWithText(copyText).assertIsDisplayed()
    }

    @Test
    fun copying_a_sent_question_puts_the_exact_text_on_the_clipboard() {
        setupChatScreen()

        longPressQuestion()
        composeTestRule.onNodeWithText(copyText).performClick()
        composeTestRule.waitForIdle()

        val clipboard = InstrumentationRegistry.getInstrumentation().targetContext
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copied = clipboard.primaryClip?.getItemAt(0)?.text?.toString()

        assertEquals(question, copied)
    }

    @Test
    fun the_copy_option_is_dismissed_after_copying() {
        setupChatScreen()

        longPressQuestion()
        composeTestRule.onNodeWithText(copyText).performClick()

        composeTestRule.onNodeWithText(copyText).assertDoesNotExist()
    }

    private fun longPressQuestion() {
        composeTestRule.onNodeWithContentDescription(questionAltText)
            .performTouchInput { longClick() }
    }

    private fun setupChatScreen() {
        val chatEntry = ChatEntry(
            id = "questionId",
            question = question,
            answer = "You can renew your passport online.",
            sources = null,
            shouldAnimate = false,
            conversationId = "conversationId",
            answerId = "answerId"
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
                    uiEvents = UiEvents(
                        onQuestionUpdated = { _ -> },
                        onSubmit = { _ -> },
                        onClear = { },
                        onFeedbackClick = { _, _, _ -> },
                        onFeedbackGiven = { _, _, _ -> }
                    ),
                    isTalkBackActive = false
                )
            }
        }
    }

    private fun getString(
        @StringRes id: Int
    ): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
}
