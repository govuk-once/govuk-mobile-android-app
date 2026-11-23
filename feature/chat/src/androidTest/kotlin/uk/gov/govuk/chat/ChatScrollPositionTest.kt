package uk.gov.govuk.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
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
 * Rotating the device recreates the chat screen at a new width, and the answers get taller or
 * shorter as their text reflows. These tests check the user is still reading the same part of the
 * conversation after rotating.
 */
@RunWith(AndroidJUnit4::class)
class ChatScrollPositionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val restorationTester = StateRestorationTester(composeTestRule)

    // The screen the chat is shown on when it's next created.
    // Not compose state, so changing it doesn't affect the chat on screen until it's recreated - like a rotation
    private var screenForNextRecreation = LANDSCAPE

    private val chatList = SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)

    @Test
    fun the_bottom_of_the_chat_in_landscape_is_still_the_bottom_in_portrait() {
        showChat()
        scrollToBottom()

        rotateTo(PORTRAIT)

        assertAtBottom()
    }

    @Test
    fun the_bottom_of_the_chat_in_portrait_is_still_the_bottom_in_landscape() {
        screenForNextRecreation = PORTRAIT
        showChat()
        scrollToBottom()

        rotateTo(LANDSCAPE)

        assertAtBottom()
    }

    private fun showChat() {
        restorationTester.setContent {
            Box(Modifier.requiredSize(screenForNextRecreation)) {
                ChatScreenContent()
            }
        }
        composeTestRule.waitForIdle()
    }

    private fun rotateTo(
        screen: DpSize
    ) {
        screenForNextRecreation = screen
        restorationTester.emulateSavedInstanceStateRestore()
        composeTestRule.waitForIdle()
    }

    private fun scrollToBottom() {
        while (canScrollFurther()) {
            composeTestRule.onNode(chatList).performTouchInput { swipeUp() }
            composeTestRule.waitForIdle()
        }
    }

    private fun assertAtBottom() {
        assertTrue("Expected the chat to be scrolled to the bottom", !canScrollFurther())
    }

    private fun canScrollFurther(): Boolean {
        val scrollRange = composeTestRule.onNode(chatList).fetchSemanticsNode()
            .config[SemanticsProperties.VerticalScrollAxisRange]
        return scrollRange.value() < scrollRange.maxValue()
    }

    @Composable
    private fun ChatScreenContent() {
        // A conversation loaded from history, so nothing animates in
        val chatEntries = (1..4).associate { number ->
            val chatEntry = ChatEntry(
                id = "question$number",
                question = "Question $number",
                answer = LONG_ANSWER,
                sources = null,
                shouldAnimate = false,
                conversationId = "conversationId",
                answerId = "answer$number"
            )
            chatEntry.id to chatEntry
        }

        GovUkTheme {
            ChatScreen(
                uiState = ChatUiState.Default(chatEntries = LinkedHashMap(chatEntries)),
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

    private companion object {
        // Roughly the size of the chat on a phone, once the status and navigation bars are taken off
        val LANDSCAPE = DpSize(400.dp, 250.dp)
        val PORTRAIT = DpSize(250.dp, 380.dp)

        val LONG_ANSWER = "You can renew your passport online. It costs less to apply online than by post. ".repeat(6)
    }
}
