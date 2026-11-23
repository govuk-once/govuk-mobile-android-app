package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Rotating the device recreates the screen at a new width. The items in the list are like paragraphs
 * of text, the narrower the screen the taller they get, so the list has to work out where the user was
 * reading rather than restoring the same pixel offset.
 *
 * Each test scrolls the list, rotates the "device" and checks the user is still looking at the same
 * content.
 */
@RunWith(AndroidJUnit4::class)
class WidthAwareLazyListStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val restorationTester = StateRestorationTester(composeTestRule)

    // The screen the list is shown on when it's next created.
    // Not compose state, so changing it doesn't affect the list on screen until it's recreated - like a rotation
    private var screenForNextRecreation = LANDSCAPE

    // Compose state, so changing it resizes the list on screen without recreating it - like resizing a window
    private var liveScreen by mutableStateOf<DpSize?>(null)

    private lateinit var listState: LazyListState
    private lateinit var coroutineScope: CoroutineScope

    @Before
    fun setup() {
        restorationTester.setContent {
            TestList(screen = liveScreen ?: screenForNextRecreation)
        }
    }

    @Test
    fun reading_halfway_through_an_item_in_landscape_is_still_halfway_through_it_in_portrait() {
        scrollToHalfwayThroughItem(3)

        rotateTo(PORTRAIT)

        assertReadingHalfwayThroughItem(3)
    }

    @Test
    fun reading_halfway_through_an_item_in_portrait_is_still_halfway_through_it_in_landscape() {
        rotateTo(PORTRAIT)
        scrollToHalfwayThroughItem(3)

        rotateTo(LANDSCAPE)

        assertReadingHalfwayThroughItem(3)
    }

    @Test
    fun rotating_and_rotating_back_returns_to_exactly_the_same_place() {
        scrollUpBy(swipes = 1)
        val originalIndex = listState.firstVisibleItemIndex
        val originalOffset = listState.firstVisibleItemScrollOffset

        rotateTo(PORTRAIT)
        rotateTo(LANDSCAPE)

        assertEquals(originalIndex, listState.firstVisibleItemIndex)
        assertEquals(originalOffset, listState.firstVisibleItemScrollOffset)
    }

    @Test
    fun the_bottom_of_the_list_in_landscape_is_still_the_bottom_in_portrait() {
        scrollToBottom()

        rotateTo(PORTRAIT)

        assertAtBottom()
    }

    @Test
    fun the_bottom_of_the_list_in_portrait_is_still_the_bottom_in_landscape() {
        rotateTo(PORTRAIT)
        scrollToBottom()

        rotateTo(LANDSCAPE)

        assertAtBottom()
    }

    @Test
    fun the_bottom_of_the_list_is_still_the_bottom_after_rotating_back_and_forth() {
        scrollToBottom()

        rotateTo(PORTRAIT)
        rotateTo(LANDSCAPE)
        rotateTo(PORTRAIT)

        assertAtBottom()
    }

    @Test
    fun reading_halfway_through_an_item_is_kept_when_the_window_is_resized() {
        scrollToHalfwayThroughItem(3)

        resizeWindowTo(PORTRAIT)

        assertReadingHalfwayThroughItem(3)
    }

    @Composable
    private fun TestList(
        screen: DpSize
    ) {
        val state = rememberWidthAwareLazyListState()
        listState = state
        coroutineScope = rememberCoroutineScope()

        Box(Modifier.requiredSize(screen)) {
            LazyColumn(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(LIST_TAG)
            ) {
                items(ITEM_COUNT) {
                    ParagraphLikeItem()
                }
            }
        }
    }

    // Like a paragraph of text - halving the width doubles the height
    @Composable
    private fun ParagraphLikeItem() {
        Box(
            Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val width = constraints.maxWidth
                    val widthDp = width / density
                    val heightDp = ITEM_AREA_DP / widthDp
                    val height = (heightDp * density).toInt()
                    val placeable = measurable.measure(constraints.copy(minHeight = height, maxHeight = height))
                    layout(width, height) { placeable.place(0, 0) }
                }
        )
    }

    private fun rotateTo(
        screen: DpSize
    ) {
        screenForNextRecreation = screen
        restorationTester.emulateSavedInstanceStateRestore()
        composeTestRule.waitForIdle()
    }

    private fun resizeWindowTo(
        screen: DpSize
    ) {
        liveScreen = screen
        composeTestRule.waitForIdle()
    }

    private fun scrollUpBy(
        swipes: Int
    ) {
        repeat(swipes) {
            composeTestRule.onNodeWithTag(LIST_TAG).performTouchInput { swipeUp() }
        }
        composeTestRule.waitForIdle()
    }

    private fun scrollToBottom() {
        while (listState.canScrollForward) {
            scrollUpBy(swipes = 1)
        }
    }

    // Animated so the list sees it as a scroll in progress, the same as the user dragging the list
    private fun scrollToHalfwayThroughItem(
        index: Int
    ) {
        composeTestRule.runOnIdle {
            coroutineScope.launch { listState.animateScrollToItem(index, itemHeight() / 2) }
        }
        composeTestRule.waitForIdle()
    }

    private fun assertReadingHalfwayThroughItem(
        index: Int
    ) {
        composeTestRule.runOnIdle {
            assertEquals(index, listState.firstVisibleItemIndex)
            assertEquals(itemHeight() / 2f, listState.firstVisibleItemScrollOffset.toFloat(), 1f)
        }
    }

    private fun assertAtBottom() {
        composeTestRule.runOnIdle {
            assertFalse("Expected the list to be scrolled to the bottom", listState.canScrollForward)
        }
    }

    private fun itemHeight(): Int = listState.layoutInfo.visibleItemsInfo.first().size

    private companion object {
        const val LIST_TAG = "list"
        const val ITEM_COUNT = 10

        // Each item is 120dp tall in landscape and 240dp tall in portrait
        const val ITEM_AREA_DP = 360f * 120f

        val LANDSCAPE = DpSize(360.dp, 180.dp)
        val PORTRAIT = DpSize(180.dp, 360.dp)
    }
}
