package uk.govuk.app.loca

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.local.DefaultLocalFeature
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.LocalAuthority

class DefaultLocalFeatureTest {

    private val localRepo = mockk<LocalRepo>(relaxed = true)
    private val localAuthority = mockk<LocalAuthority>(relaxed = true)

    private lateinit var localFeature: DefaultLocalFeature

    @Before
    fun setup() {
        localFeature = DefaultLocalFeature(localRepo)
    }

    @Test
    fun `Null local authority emits false`() {
        every { localRepo.localAuthority } returns flowOf(null)

        runTest {
            assertFalse(localFeature.hasLocalAuthority().first())
        }
    }

    @Test
    fun `Local authority emits true`() {
        every { localRepo.localAuthority } returns flowOf(localAuthority)

        runTest {
            assertTrue(localFeature.hasLocalAuthority().first())
        }
    }

    @Test
    fun `Clear clears the repo`() {
        runTest {
            localFeature.clear()

            coVerify { localRepo.clear() }
        }
    }

}