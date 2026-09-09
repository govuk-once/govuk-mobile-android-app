package uk.govuk.app.local.data

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.local.data.local.model.LocalAuthorityEntity
import uk.govuk.app.local.data.local.model.LocalAuthorityParentEntity
import uk.govuk.app.local.data.remote.model.RemoteAddress
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.domain.model.Address
import uk.govuk.app.local.domain.model.LocalAuthority

class LocalAuthorityMapperTest {

    @Test
    fun `Map from remote local authority with parent`() {
        val remoteLocalAuthority = RemoteLocalAuthority(
            name = "name",
            homePageUrl = "url",
            tier = "tier",
            slug = "slug",
            parent = RemoteLocalAuthority(
                name = "parent name",
                homePageUrl = "parent url",
                tier = "parent tier",
                slug = "parent slug",
            )
        )

        val expected = LocalAuthority(
            name = "name",
            url = "url",
            slug = "slug",
            parent = LocalAuthority(
                name = "parent name",
                url = "parent url",
                slug = "parent slug"
            )
        )

        assertEquals(expected, remoteLocalAuthority.toLocalAuthority())
    }

    @Test
    fun `Map from remote local authority without parent`() {
        val remoteLocalAuthority = RemoteLocalAuthority(
            name = "name",
            homePageUrl = "url",
            tier = "tier",
            slug = "slug"
        )

        val expected = LocalAuthority(
            name = "name",
            url = "url",
            slug = "slug"
        )

        assertEquals(expected, remoteLocalAuthority.toLocalAuthority())
    }

    @Test
    fun `Map from local authority entity with parent`() {
        val entity = LocalAuthorityEntity(
            name = "name", url = "url", slug = "slug",
            parent = LocalAuthorityParentEntity(name = "parent name", url = "parent url", slug = "parent slug")
        )

        val expected = LocalAuthority(
            name = "name",
            url = "url",
            slug = "slug",
            parent = LocalAuthority(
                name = "parent name",
                url = "parent url",
                slug = "parent slug"
            )
        )

        assertEquals(expected, entity.toLocalAuthority())
    }

    @Test
    fun `Map from local authority entity without parent`() {
        val entity = LocalAuthorityEntity(
            name = "name", url = "url", slug = "slug",
            parent = null
        )

        val expected = LocalAuthority(
            name = "name",
            url = "url",
            slug = "slug"
        )

        assertEquals(expected, entity.toLocalAuthority())
    }

    @Test
    fun `Map from address`() {
        val remoteAddress = RemoteAddress(
            address = "address",
            slug = "slug",
            name = "name"
        )

        val expected = Address(
            address = "address",
            slug = "slug",
            name = "name"
        )

        assertEquals(expected, remoteAddress.toAddress())
    }

}
