package uk.govuk.app.local.data.store

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uk.govuk.app.local.data.local.LocalDao
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.local.model.LocalAuthorityEntity
import uk.govuk.app.local.data.local.model.LocalAuthorityParentEntity
import uk.govuk.app.local.domain.model.LocalAuthority

class LocalDataSourceTest {

    private val dao = mockk<LocalDao>(relaxed = true)
    private lateinit var dataSource: LocalDataSource

    @Before
    fun setup() {
        dataSource = LocalDataSource(dao)
    }

    @Test
    fun `Given a local authority in db, when get local authority, then emit the local authority`() {
        val entity = LocalAuthorityEntity(
            name = "name", url = "url", slug = "slug",
            parent = LocalAuthorityParentEntity(name = "parentName", url = "parentUrl", slug = "parentSlug")
        )
        every { dao.getLocalAuthority() } returns flowOf(entity)

        runTest {
            val result = dataSource.localAuthority.first()
            assertEquals("name", result?.name)
            assertEquals("url", result?.url)
            assertEquals("slug", result?.slug)
            assertEquals("parentName", result?.parent?.name)
            assertEquals("parentUrl", result?.parent?.url)
            assertEquals("parentSlug", result?.parent?.slug)
        }
    }

    @Test
    fun `Given no local authority in db, when get local authority, then emit null`() {
        every { dao.getLocalAuthority() } returns flowOf(null)

        runTest {
            assertNull(dataSource.localAuthority.first())
        }
    }

    @Test
    fun `Given a local authority with parent, when insert or replace, then insert into db with parent fields`() {
        val localAuthority = LocalAuthority(
            name = "name", url = "url", slug = "slug",
            parent = LocalAuthority(name = "parentName", url = "parentUrl", slug = "parentSlug")
        )

        runTest {
            dataSource.insertOrReplace(localAuthority)

            coVerify {
                dao.insertOrReplace(match {
                    it.name == "name" && it.url == "url" && it.slug == "slug" &&
                    it.parent?.name == "parentName" && it.parent?.url == "parentUrl" && it.parent?.slug == "parentSlug"
                })
            }
        }
    }

    @Test
    fun `Given a local authority without parent, when insert or replace, then insert into db with null parent fields`() {
        val localAuthority = LocalAuthority(name = "name", url = "url", slug = "slug")

        runTest {
            dataSource.insertOrReplace(localAuthority)

            coVerify {
                dao.insertOrReplace(match {
                    it.name == "name" && it.parent == null
                })
            }
        }
    }

    @Test
    fun `Given the data source is cleared, then delete all from db`() {
        runTest {
            dataSource.clear()
            coVerify { dao.deleteAll() }
        }
    }
}
