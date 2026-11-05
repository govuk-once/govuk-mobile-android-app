package uk.govuk.app.local

import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority

@OptIn(ExperimentalCoroutinesApi::class)
class LocalLookupViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val localRepo = mockk<LocalRepo>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val remoteLocalAuthority = mockk<RemoteLocalAuthority>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LocalLookupViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LocalLookupViewModel(analyticsClient, localRepo, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalLookupScreen",
                screenName = "Local Lookup",
                title = "Local Lookup"
            )
        }
    }

    @Test
    fun `Given a postcode lookup, then log analytics`() {
        viewModel.onSearchPostcode("button text", "")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )
        }
    }

    @Test
    fun `Given a uiState that is an error, when the postcode is changed, then it clears the error`() {
        viewModel.onSearchPostcode("text", " ")

        viewModel.onPostcodeChange()

        assertNull(viewModel.uiState.value)
    }

    @Test
    fun `onSearchPostcode calls analytics client and fetches local authority - emits local authority nav event`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.fetchLocalAuthority(postcode)
        } returns Success(LocalAuthorityResult.LocalAuthority(remoteLocalAuthority))

        val events = mutableListOf<NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.toList(events)
        }

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.fetchLocalAuthority(postcode) }

        assertEquals(1, events.size)
        assertEquals(NavigationEvent.LocalAuthoritySelected, events.first())
    }

    @Test
    fun `onSearchPostcode calls analytics client and fetches local authority - emits addresses nav event`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.fetchLocalAuthority(postcode)
        } returns Success(LocalAuthorityResult.Addresses(emptyList()))

        val events = mutableListOf<NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.toList(events)
        }

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.fetchLocalAuthority(postcode) }

        assertEquals(1, events.size)
        assertEquals(NavigationEvent.Addresses(postcode), events.first())
    }

    @Test
    fun `onSearchPostcode calls analytics client and fetches local authority - returns invalid postcode - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.fetchLocalAuthority(postcode)
        } returns Success(LocalAuthorityResult.InvalidPostcode)

        val errorMessage = "invalid postcode error"

        every { context.getString(R.string.local_invalid_postcode_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify {
            analyticsClient.buttonClick(text = buttonText, section = "Local")
            analyticsClient.screenView(
                screenClass = "LocalLookupScreen",
                screenName = "Local Lookup",
                title = errorMessage
            )
        }
        coVerify { localRepo.fetchLocalAuthority(postcode) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_invalid_postcode_message, uiState.message)
    }

    @Test
    fun `onSearchPostcode calls analytics client and fetches local authority - returns postcode not found - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.fetchLocalAuthority(postcode)
        } returns Success(LocalAuthorityResult.PostcodeNotFound)

        val errorMessage = "postcode not found error"

        every { context.getString(R.string.local_not_found_postcode_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.fetchLocalAuthority(postcode) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_not_found_postcode_message, uiState.message)
    }

    @Test
    fun `onSearchPostcode calls analytics client - does not fetch local authority - returns no postcode - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = " "

        val errorMessage = "no postcode error"

        every { context.getString(R.string.local_no_postcode_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify {
            analyticsClient.buttonClick(text = buttonText, section = "Local")
            analyticsClient.screenView(
                screenClass = "LocalLookupScreen",
                screenName = "Local Lookup",
                title = errorMessage
            )
        }
        coVerify(exactly = 0) { localRepo.fetchLocalAuthority(any()) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_no_postcode_message, uiState.message)
    }

    @Test
    fun `onSearchPostcode calls analytics client and fetches local authority - returns postcode empty or null - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = ""

        coEvery {
            localRepo.fetchLocalAuthority(postcode)
        } returns Success(LocalAuthorityResult.PostcodeEmptyOrNull)

        val errorMessage = "postcode empty or null error"

        every { context.getString(R.string.local_no_postcode_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify(exactly = 0) { localRepo.fetchLocalAuthority(postcode) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_no_postcode_message, uiState.message)
    }

    @Test
    fun `onSearchPostcode calls analytics client and fetches local authority - returns rate limit message - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.fetchLocalAuthority(postcode)
        } returns Success(LocalAuthorityResult.ApiNotResponding)

        val errorMessage = "rate limiting error"

        every { context.getString(R.string.local_rate_limit_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.fetchLocalAuthority(postcode) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_rate_limit_message, uiState.message)
    }

    @Test
    fun `onSearchPostcode calls analytics client and fetches local authority - returns device not connected message - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.fetchLocalAuthority(postcode)
        } returns Success(LocalAuthorityResult.DeviceNotConnected)

        val errorMessage = "not connected error"

        every { context.getString(R.string.local_not_connected_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.fetchLocalAuthority(postcode) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_not_connected_message, uiState.message)
    }
}