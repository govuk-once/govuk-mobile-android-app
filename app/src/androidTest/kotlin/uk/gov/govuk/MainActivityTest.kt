package uk.gov.govuk

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val intent =
        Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun given_the_MainActivity_is_launched_When_onCreate_is_called_then_the_intent_flags_should_be_NEW_TASK_and_CLEAR_TASK() {
        val scenario = ActivityScenario.launch<MainActivity>(intent)
        scenario.onActivity { activity ->
            runTest {
                assertEquals(
                    activity.intent.flags,
                    FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                )
            }
        }
    }

    @Test
    fun given_the_MainActivity_is_launched_When_onCreate_is_called_then_the_intentFlow_has_a_cached_value() {
        val scenario = ActivityScenario.launch<MainActivity>(intent)
        scenario.onActivity { activity ->
            runTest {
                val deferredCacheSize = async {
                    activity.intentFlow.replayCache.size
                }
                val deferredIntent = async {
                    activity.intentFlow.first()
                }
                assertEquals(1, deferredCacheSize.await())
                assertEquals(activity.intent, deferredIntent.await())
            }
        }
    }

    @Test
    fun given_the_MainActivity_is_launched_then_recreated_When_onCreate_is_called_then_the_intentFlow_has_an_empty_cache() {
        val scenario = ActivityScenario.launch<MainActivity>(intent)
        scenario.recreate()
        scenario.onActivity { activity ->
            runTest {
                assertTrue(activity.intentFlow.replayCache.isEmpty())
            }
        }
    }

    @Test
    fun given_the_MainActivity_is_launched_When_onNewIntent_is_called_then_the_intentFlow_has_has_a_cached_value_with_the_same_fields() {
        val scenario = ActivityScenario.launch<MainActivity>(intent)
        scenario.recreate()
        var mainActivity: MainActivity? = null
        scenario.onActivity { activity ->
            mainActivity = activity
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_SINGLE_TOP
            InstrumentationRegistry.getInstrumentation().targetContext.startActivity(intent)
        }
        runTest {
            mainActivity?.let {
                val deferredCacheSize = async {
                    it.intentFlow.replayCache.size
                }
                val deferredIntent = async {
                    it.intentFlow.first().toUri(0)
                }
                assertEquals(1, deferredCacheSize.await())
                assertEquals(intent.toUri(0), deferredIntent.await())
            }
        }
    }
}
