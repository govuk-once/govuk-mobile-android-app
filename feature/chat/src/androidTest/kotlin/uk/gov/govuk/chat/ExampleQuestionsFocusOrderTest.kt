package uk.gov.govuk.chat

import androidx.annotation.StringRes
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.chat.ui.AnalyticsEvents
import uk.gov.govuk.chat.ui.ChatScreen
import uk.gov.govuk.chat.ui.UiEvents
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.theme.GovUkTheme

/**
 * TalkBack must read the example questions on the Chat screen in the top-to-bottom
 * order they're shown in, not whatever order the accessibility service happens to
 * produce. We use `isTraversalGroup` and `traversalIndex` semantics on each question
 * card to enforce this. However, it's easy to silently break this by restructuring
 * the card's composable.
 * These tests guard against that happening.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class ExampleQuestionsFocusOrderTest {
    private val prompt = getString(R.string.example_question_prompt)
    private val questions = listOf(
        "What is Universal Credit?",
        "How do I renew my passport?",
        "How do I tax my vehicle?"
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun each_example_question_card_carries_an_ascending_traversal_index() {
        setupChatScreen()
        waitForExampleQuestionsToAppear()

        questions.forEachIndexed { index, _ ->
            val node = composeTestRule
                .onNodeWithTag("exampleQuestion_$index", useUnmergedTree = true)
                .fetchSemanticsNode()

            assertTrue(
                "exampleQuestion_$index should be a traversal group check its traversal index is correct",
                node.config.getOrElse(SemanticsProperties.IsTraversalGroup) { false }
            )
            assertEquals(
                "exampleQuestion_$index should be ordered at position $index",
                index.toFloat(),
                node.config.getOrElse(SemanticsProperties.TraversalIndex) { -1f }
            )
        }
    }

    @Test
    fun each_example_question_is_announced_with_the_try_asking_prefix_in_order() {
        setupChatScreen()
        waitForExampleQuestionsToAppear()

        questions.forEach { question ->
            composeTestRule
                .onNodeWithContentDescription("$prompt $question")
                .assertHasClickAction()
        }
    }

    @Test
    fun tapping_an_example_question_submits_that_question_and_no_other() {
        var submittedQuestion: String? = null
        setupChatScreen(onSubmit = { submittedQuestion = it })
        waitForExampleQuestionsToAppear()

        val tappedQuestion = questions[1]
        composeTestRule
            .onNodeWithContentDescription("$prompt $tappedQuestion")
            .performClick()

        assertEquals(tappedQuestion, submittedQuestion)
    }

    private fun waitForExampleQuestionsToAppear() {
        composeTestRule.waitUntilAtLeastOneExists(
            hasTestTag("exampleQuestion_0"),
            timeoutMillis = 5_000
        )
    }

    private fun setupChatScreen(onSubmit: (String) -> Unit = { }) {
        composeTestRule.setContent {
            GovUkTheme {
                ChatScreen(
                    uiState = ChatUiState.Default(isLoading = false),
                    analyticsEvents = AnalyticsEvents(
                        onPageView = { _, _, _ -> },
                        onNavigationActionItemClicked = { _, _ -> },
                        onFunctionActionItemClicked = { _, _, _ -> },
                        onQuestionSubmit = { },
                        onMarkdownLinkClicked = { _, _ -> },
                        onSourcesExpanded = { }
                    ),
                    launchBrowser = { _ -> },
                    hasConversation = false,
                    chatUrls = ChatUrls("", "", "", ""),
                    chatExampleQuestions = questions,
                    isImeVisible = false,
                    uiEvents = UiEvents(
                        onQuestionUpdated = { _ -> },
                        onSubmit = onSubmit,
                        onClear = { }
                    )
                )
            }
        }
    }

    private fun getString(@StringRes id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }
}
