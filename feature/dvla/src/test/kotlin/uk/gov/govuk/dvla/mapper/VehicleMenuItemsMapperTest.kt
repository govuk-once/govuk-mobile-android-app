package uk.gov.govuk.dvla.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.dvlaUrls
import uk.gov.govuk.dvla.util.StringProvider

class VehicleMenuItemsMapperTest {
    private val stringProvider = mockk<StringProvider>()

    private val mapper = VehicleMenuItemsMapper(stringProvider)

    @Before
    fun setup() {
        every { stringProvider.getString(any<Int>(), *anyVararg()) } returns ""
    }

    @Test
    fun `Given dvlaUrls is null, then menu items is empty`() {
        val result = mapper.buildMenuItems(hasSorn = true, isTaxed = true, dvlaUrls = null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `Given vehicle has no SORN, then menu contains Register as off road`() {
        val result = mapper.buildMenuItems(hasSorn = false, isTaxed = true, dvlaUrls = dvlaUrls)
        assertTrue(result.any { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.makeSorn })
    }

    @Test
    fun `Given vehicle has no SORN, then menu does not contain SORN rules`() {
        val result = mapper.buildMenuItems(hasSorn = false, isTaxed = true, dvlaUrls = dvlaUrls)
        assertTrue(result.none { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.sornRules })
    }

    @Test
    fun `Given vehicle has SORN, then menu contains SORN rules`() {
        val result = mapper.buildMenuItems(hasSorn = true, isTaxed = true, dvlaUrls = dvlaUrls)
        assertTrue(result.any { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.sornRules })
    }

    @Test
    fun `Given vehicle has SORN, then menu does not contain Register as off road`() {
        val result = mapper.buildMenuItems(hasSorn = true, isTaxed = true, dvlaUrls = dvlaUrls)
        assertTrue(result.none { (it.action as? MenuAction.WebLink)?.url == dvlaUrls.makeSorn })
    }

    @Test
    fun `Given dvlaUrls is non-null, then common menu items are always present`() {
        val result = mapper.buildMenuItems(hasSorn = true, isTaxed = true, dvlaUrls = dvlaUrls)
        val urls = result.map { (it.action as? MenuAction.WebLink)?.url }
        assertTrue(urls.contains(dvlaUrls.soldVehicle))
        assertTrue(urls.contains(dvlaUrls.getLogbook))
        assertTrue(urls.contains(dvlaUrls.changeLogbookAddress))
    }

    @Test
    fun `Given vehicle has no SORN then the make sorn menu item is present and the sorn rules menu item is not present`() {
        val result = mapper.buildMenuItems(hasSorn = false, isTaxed = true, dvlaUrls = dvlaUrls)
        val urls = result.map { (it.action as MenuAction.WebLink).url }
        assertTrue(urls.contains(dvlaUrls.makeSorn))
        assertFalse(urls.contains(dvlaUrls.sornRules))
    }

    @Test
    fun `Given vehicle has SORN then the make sorn menu item is not present and the sorn rules menu item is present`() {
        val result = mapper.buildMenuItems(hasSorn = true, isTaxed = true, dvlaUrls = dvlaUrls)
        val urls = result.map { (it.action as MenuAction.WebLink).url }
        assertTrue(urls.contains(dvlaUrls.sornRules))
        assertFalse(urls.contains(dvlaUrls.makeSorn))
    }

    @Test
    fun `Given vehicle is taxed and not SORN then the cancel tax button is present`() {
        val result = mapper.buildMenuItems(hasSorn = false, isTaxed = true, dvlaUrls = dvlaUrls)
        val urls = result.map { (it.action as MenuAction.WebLink).url }
        assertTrue(urls.contains(dvlaUrls.cancelTax))
    }

    @Test
    fun `Given vehicle has SORN and is taxed, then cancel tax is not present`() {
        val result = mapper.buildMenuItems(hasSorn = true, isTaxed = true, dvlaUrls = dvlaUrls)
        val urls = result.map { (it.action as MenuAction.WebLink).url }
        assertFalse(urls.contains(dvlaUrls.cancelTax))
    }

    @Test
    fun `Given vehicle has SORN and is taxed, then register off road is not present`() {
      val result = mapper.buildMenuItems(hasSorn = true, isTaxed = true, dvlaUrls = dvlaUrls)
      val urls = result.map { (it.action as MenuAction.WebLink).url }
      assertFalse(urls.contains(dvlaUrls.makeSorn))
    }

    @Test
    fun `Given vehicle is not taxed then the cancel tax button is not present`() {
        val result = mapper.buildMenuItems(hasSorn = true, isTaxed = false, dvlaUrls = dvlaUrls)
        val urls = result.map { (it.action as MenuAction.WebLink).url }
        assertFalse(urls.contains(dvlaUrls.cancelTax))
    }
}