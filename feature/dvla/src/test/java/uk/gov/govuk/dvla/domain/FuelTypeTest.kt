package uk.gov.govuk.dvla.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.common.FuelType as RemoteFuelType

class FuelTypeTest {
    @Test
    fun `Verify a null status is OTHER`() {
        val remote: RemoteFuelType? = null
        assertEquals(FuelType.OTHER, remote.toDomain())
    }

    @Test
    fun `Verify PETROL status is PETROL`() {
        val remote = RemoteFuelType.PETROL
        assertEquals(FuelType.PETROL, remote.toDomain())
    }

    @Test
    fun `Verify DIESEL status is DIESEL`() {
        val remote = RemoteFuelType.DIESEL
        assertEquals(FuelType.DIESEL, remote.toDomain())
    }

    @Test
    fun `Verify ELECTRICITY status is ELECTRICITY`() {
        val remote = RemoteFuelType.ELECTRICITY
        assertEquals(FuelType.ELECTRICITY, remote.toDomain())
    }

    @Test
    fun `Verify STEAM status is STEAM`() {
        val remote = RemoteFuelType.STEAM
        assertEquals(FuelType.STEAM, remote.toDomain())
    }

    @Test
    fun `Verify GAS status is GAS`() {
        val remote = RemoteFuelType.GAS
        assertEquals(FuelType.GAS, remote.toDomain())
    }

    @Test
    fun `Verify PETROL_GAS status is PETROL_GAS`() {
        val remote = RemoteFuelType.PETROL_GAS
        assertEquals(FuelType.PETROL_GAS, remote.toDomain())
    }

    @Test
    fun `Verify GAS_BI_FUEL status is GAS_BI_FUEL`() {
        val remote = RemoteFuelType.GAS_BI_FUEL
        assertEquals(FuelType.GAS_BI_FUEL, remote.toDomain())
    }

    @Test
    fun `Verify HYBRID_ELECTRIC status is HYBRID_ELECTRIC`() {
        val remote = RemoteFuelType.HYBRID_ELECTRIC
        assertEquals(FuelType.HYBRID_ELECTRIC, remote.toDomain())
    }

    @Test
    fun `Verify GAS_DIESEL status is GAS_DIESEL`() {
        val remote = RemoteFuelType.GAS_DIESEL
        assertEquals(FuelType.GAS_DIESEL, remote.toDomain())
    }

    @Test
    fun `Verify FUEL_CELLS status is FUEL_CELLS`() {
        val remote = RemoteFuelType.FUEL_CELLS
        assertEquals(FuelType.FUEL_CELLS, remote.toDomain())
    }

    @Test
    fun `Verify ELECTRIC_DIESEL status is ELECTRIC_DIESEL`() {
        val remote = RemoteFuelType.ELECTRIC_DIESEL
        assertEquals(FuelType.ELECTRIC_DIESEL, remote.toDomain())
    }

    @Test
    fun `Verify OTHER status is OTHER`() {
        val remote = RemoteFuelType.OTHER
        assertEquals(FuelType.OTHER, remote.toDomain())
    }
}
