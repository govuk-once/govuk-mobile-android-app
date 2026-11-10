package uk.govuk.app.local

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.LocalAuthority

@OptIn(ExperimentalCoroutinesApi::class)
class LocalConfirmationViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val localRepo = mockk<LocalRepo>(relaxed = true)

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LocalConfirmationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LocalConfirmationViewModel(localRepo, analyticsClient)
    }

    @Test
    fun `Forward cached local authority from repo`() {
        val localAuthority = LocalAuthority(
            name = "name",
            url = "url",
            slug = "slug"
        )

        every {
            localRepo.cachedLocalAuthority
        } returns localAuthority

        assertEquals(localAuthority, viewModel.localAuthority)
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalConfirmationScreen",
                screenName = "Local Confirmation",
                title = "Local Confirmation"
            )
        }
    }

    @Test
    fun `Given on done, then log analytics and select local authority`() {
        viewModel.onDone("button text")

        coVerify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )

            localRepo.selectLocalAuthority()
        }
    }
}