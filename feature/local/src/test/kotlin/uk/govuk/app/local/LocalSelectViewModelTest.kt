package uk.govuk.app.local

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.Address
import uk.govuk.app.local.domain.model.LocalAuthority

@OptIn(ExperimentalCoroutinesApi::class)
class LocalSelectViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val localRepo = mockk<LocalRepo>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LocalSelectViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LocalSelectViewModel(analyticsClient, localRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a select by Local Authority page view, then log analytics`() {
        viewModel.onSelectByLocalAuthorityPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalAuthoritySelectScreen",
                screenName = "Local Select",
                title = "Local Select"
            )
        }
    }

    @Test
    fun `Given a select by Address page view, then log analytics`() {
        viewModel.onSelectByAddressPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalAddressSelectScreen",
                screenName = "Local Select",
                title = "Local Select"
            )
        }
    }

    @Test
    fun `Given a select by Address button click, then log analytics`() {
        viewModel.onSelectByAddressButtonClick("button text")

        coVerify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )
        }
    }

    @Test
    fun `Given slug selection, then log analytics and cache local authority`() {
        viewModel.updateLocalAuthority("button text", "dorset")

        coVerify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )

            localRepo.cacheLocalAuthority("dorset")
        }
    }

    @Test
    fun `Given local authorities, then sort by name`() {
        val localAuthorityZ = LocalAuthority("Z Authority", "url1", "slug-one")
        val localAuthorityA = LocalAuthority("A Authority", "url2", "slug-two")

        coEvery {
            localRepo.localAuthorities
        } returns listOf(localAuthorityZ, localAuthorityA)

        val actual = viewModel.localAuthorities

        assert(actual[0] == localAuthorityA)
        assert(actual[1] == localAuthorityZ)
    }


    @Test
    fun `Given addresses, then sort by address`() {
        val addressZ = Address("Z Test Street, AB1C1DE", "slug-one", "Slug One")
        val addressA = Address("A Test Street, AB1C1DE", "slug-two", "Slug Two")

        coEvery {
            localRepo.addresses
        } returns listOf(addressZ, addressA)

        val actual = viewModel.addresses

        assert(actual[0] == addressA)
        assert(actual[1] == addressZ)
    }
}
