package uk.gov.govuk.dvla.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.common.VehicleColour as RemoteVehicleColour

class VehicleColourTest {
    @Test
    fun `Verify a null status is UNKNOWN`() {
        val remote: RemoteVehicleColour? = null
        assertEquals(VehicleColour.UNKNOWN, remote.toDomain())
    }

    @Test
    fun `Verify a BROWN status is BROWN`() {
        val remote = RemoteVehicleColour.BROWN
        assertEquals(VehicleColour.BROWN, remote.toDomain())
    }

    @Test
    fun `Verify a BRONZE status is BRONZE`() {
        val remote = RemoteVehicleColour.BRONZE
        assertEquals(VehicleColour.BRONZE, remote.toDomain())
    }

    @Test
    fun `Verify a RED status is RED`() {
        val remote = RemoteVehicleColour.RED
        assertEquals(VehicleColour.RED, remote.toDomain())
    }

    @Test
    fun `Verify a PINK status is PINK`() {
        val remote = RemoteVehicleColour.PINK
        assertEquals(VehicleColour.PINK, remote.toDomain())
    }

    @Test
    fun `Verify a ORANGE status is ORANGE`() {
        val remote = RemoteVehicleColour.ORANGE
        assertEquals(VehicleColour.ORANGE, remote.toDomain())
    }

    @Test
    fun `Verify a YELLOW status is YELLOW`() {
        val remote = RemoteVehicleColour.YELLOW
        assertEquals(VehicleColour.YELLOW, remote.toDomain())
    }

    @Test
    fun `Verify a GOLD status is GOLD`() {
        val remote = RemoteVehicleColour.GOLD
        assertEquals(VehicleColour.GOLD, remote.toDomain())
    }

    @Test
    fun `Verify a GREEN status is GREEN`() {
        val remote = RemoteVehicleColour.GREEN
        assertEquals(VehicleColour.GREEN, remote.toDomain())
    }

    @Test
    fun `Verify a BLUE status is BLUE`() {
        val remote = RemoteVehicleColour.BLUE
        assertEquals(VehicleColour.BLUE, remote.toDomain())
    }

    @Test
    fun `Verify a PURPLE status is PURPLE`() {
        val remote = RemoteVehicleColour.PURPLE
        assertEquals(VehicleColour.PURPLE, remote.toDomain())
    }

    @Test
    fun `Verify a GREY status is GREY`() {
        val remote = RemoteVehicleColour.GREY
        assertEquals(VehicleColour.GREY, remote.toDomain())
    }

    @Test
    fun `Verify a SILVER status is SILVER`() {
        val remote = RemoteVehicleColour.SILVER
        assertEquals(VehicleColour.SILVER, remote.toDomain())
    }

    @Test
    fun `Verify a WHITE status is WHITE`() {
        val remote = RemoteVehicleColour.WHITE
        assertEquals(VehicleColour.WHITE, remote.toDomain())
    }

    @Test
    fun `Verify a BLACK status is BLACK`() {
        val remote = RemoteVehicleColour.BLACK
        assertEquals(VehicleColour.BLACK, remote.toDomain())
    }

    @Test
    fun `Verify a MULTI_COLOUR status is MULTI_COLOUR`() {
        val remote = RemoteVehicleColour.MULTI_COLOUR
        assertEquals(VehicleColour.MULTI_COLOUR, remote.toDomain())
    }

    @Test
    fun `Verify a BEIGE status is BEIGE`() {
        val remote = RemoteVehicleColour.BEIGE
        assertEquals(VehicleColour.BEIGE, remote.toDomain())
    }

    @Test
    fun `Verify a MAROON status is MAROON`() {
        val remote = RemoteVehicleColour.MAROON
        assertEquals(VehicleColour.MAROON, remote.toDomain())
    }

    @Test
    fun `Verify a TURQUOISE status is TURQUOISE`() {
        val remote = RemoteVehicleColour.TURQUOISE
        assertEquals(VehicleColour.TURQUOISE, remote.toDomain())
    }

    @Test
    fun `Verify a CREAM status is CREAM`() {
        val remote = RemoteVehicleColour.CREAM
        assertEquals(VehicleColour.CREAM, remote.toDomain())
    }

    @Test
    fun `Verify a NOT_STATED status is NOT_STATED`() {
        val remote = RemoteVehicleColour.NOT_STATED
        assertEquals(VehicleColour.NOT_STATED, remote.toDomain())
    }
}
