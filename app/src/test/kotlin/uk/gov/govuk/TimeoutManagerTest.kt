package uk.gov.govuk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class TimeoutManagerTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var timeoutManager: TimeoutManager

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        timeoutManager = TimeoutManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Warning and Timeout is triggered after interval`() {
        runTest {
            var warning = false
            var timedOut = false

            timeoutManager.onUserInteraction(
                interactionTime = 1000,
                warningInterval = 10,
                timeoutInterval = 20,
                onWarning = {
                    warning = true
                },
                onTimeout = {
                    timedOut = true
                }
            )

            delay(11)
            assertTrue(warning)
            assertFalse(timedOut)

            delay(10)
            assertTrue(timedOut)
        }
    }

    @Test
    fun `Interaction events are throttled if received in less than a second`() {
        runTest {
            var timedOut = false

            timeoutManager.onUserInteraction(
                interactionTime = 1000,
                warningInterval = 5,
                timeoutInterval = 10,
                onWarning = { },
                onTimeout = {
                    timedOut = true
                }
            )

            delay(5)

            timeoutManager.onUserInteraction(
                interactionTime = 1999,
                warningInterval = 5,
                timeoutInterval = 10,
                onWarning = { },
                onTimeout = {
                    timedOut = true
                }
            )

            delay(6)

            // Timeout is triggered from the first interaction, the second interaction is ignored
            // as it occurs within the throttle threshold of 1 second
            assertTrue(timedOut)
        }
    }

}