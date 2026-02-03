package uk.gov.govuk.chat

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
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
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ActionMenuTest {
    val moreOptionsText = getString(id = R.string.action_alt)
    val aboutText = getString(R.string.action_about)
    val privacyText = getString(R.string.action_privacy)
    val feedbackText = getString(R.string.action_feedback)
    val clearText = getString(id = R.string.action_clear)
    val closeText = getString(R.string.action_close)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun the_about_menu_item_is_always_shown() {
        setupChatScreen()

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText(aboutText).assertIsDisplayed()
    }

    @Test
    fun the_privacy_notice_menu_item_is_always_shown() {
        setupChatScreen()

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText(privacyText).assertIsDisplayed()
    }

    @Test
    fun the_give_feedback_menu_item_is_always_shown() {
        setupChatScreen()

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText(feedbackText).assertIsDisplayed()
    }

    @Test
    fun when_there_is_an_active_conversation_the_clear_chat_menu_item_is_shown() {
        setupChatScreen(hasConversation = true)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText(clearText).assertIsDisplayed()
    }

    @Test
    fun when_there_is_no_active_conversation_the_clear_chat_menu_item_is_not_shown() {
        setupChatScreen(hasConversation = false)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText(clearText).assertDoesNotExist()
    }

    @Test
    fun when_talkBack_is_active_the_close_menu_item_is_shown() {
        setupChatScreen(isTalkBackActive = true)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText(closeText).assertIsDisplayed()
    }

    @Test
    fun when_talkBack_is_inactive_the_close_menu_item_is_not_shown() {
        setupChatScreen(isTalkBackActive = false)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText(closeText).assertDoesNotExist()
    }

    private fun setupChatScreen(
        isTalkBackActive: Boolean = Random.nextBoolean(),
        hasConversation: Boolean = Random.nextBoolean()
    ) {
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
                    hasConversation = hasConversation,
                    chatUrls = ChatUrls("", "", "", ""),
                    uiEvents = UiEvents(
                        onQuestionUpdated = { _ -> },
                        onSubmit = { _ -> },
                        onClear = { }
                    ),
                    isTalkBackActive = isTalkBackActive
                )
            }
        }
    }

    private fun openMoreOptionsMenu() {
        composeTestRule.onNodeWithContentDescription(moreOptionsText).performClick()
    }

    private fun getString(@StringRes id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }
}
