package uk.govuk.app.local.data.store

import io.mockk.coEvery
import io.mockk.mockk
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.local.LocalRealmProvider
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
import uk.govuk.app.local.data.local.model.StoredLocalAuthorityParent
import uk.govuk.app.local.domain.model.LocalAuthority

class LocalDataSourceTest {
    private val realmProvider = mockk<LocalRealmProvider>(relaxed = true)

    private lateinit var realm: Realm

    @Before
    fun setup() {
        val config =
            RealmConfiguration.Builder(
                schema =
                    setOf(
                        StoredLocalAuthority::class,
                        StoredLocalAuthorityParent::class
                    )
            )
                .inMemory() // In-memory Realm for testing
                .build()

        // Open the Realm instance
        realm = Realm.open(config)

        coEvery { realmProvider.open() } returns realm
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun `Given a local authority, when get the local authority, then we emit the local authority`() {
        runTest {
            realm.write {
                copyToRealm(
                    StoredLocalAuthority().apply {
                        name = "name"
                        url = "url"
                        slug = "slug"
                        parent = StoredLocalAuthorityParent().apply {
                            name = "parentName"
                            url = "parentUrl"
                            slug = "parentSlug"
                        }
                    }
                )
            }

            val localDataSource = LocalDataSource(realmProvider)

            val localAuthority = localDataSource.localAuthority.first()
            val parent = localAuthority?.parent

            assertEquals("name", localAuthority?.name)
            assertEquals("url", localAuthority?.url)
            assertEquals("slug", localAuthority?.slug)
            assertEquals("parentName", parent?.name)
            assertEquals("parentUrl", parent?.url)
            assertEquals("parentSlug", parent?.slug)
        }
    }

    @Test
    fun `Given no local authorities, when a local authority is selected by the user, then we create the local authority`() {
        runTest {
            val localDataSource = LocalDataSource(realmProvider)

            localDataSource.insertOrReplace(
                LocalAuthority(
                    "name",
                    "url",
                    "slug",
                    LocalAuthority(
                        "parentName",
                        "parentUrl",
                        "parentSlug"
                    )
                )
            )

            val localAuthority = localDataSource.localAuthority.first()
            val parent = localAuthority?.parent

            assertEquals("name", localAuthority?.name)
            assertEquals("url", localAuthority?.url)
            assertEquals("slug", localAuthority?.slug)
            assertEquals("parentName", parent?.name)
            assertEquals("parentUrl", parent?.url)
            assertEquals("parentSlug", parent?.slug)
        }
    }

    @Test
    fun `Given an existing local authority, when a user selects a local authority, then we replace the existing local authority`() {
        runTest {
            val localDataSource = LocalDataSource(realmProvider)

            localDataSource.insertOrReplace(
                LocalAuthority(
                    "name",
                    "url",
                    "slug",
                    LocalAuthority(
                        "parentName",
                        "parentUrl",
                        "parentSlug"
                    )
                )
            )

            localDataSource.insertOrReplace(
                LocalAuthority(
                    "newName",
                    "newUrl",
                    "newSlug"
                )
            )

            val localAuthority = localDataSource.localAuthority.first()

            assertEquals("newName", localAuthority?.name)
            assertEquals("newUrl", localAuthority?.url)
            assertEquals("newSlug", localAuthority?.slug)
            assertNull(localAuthority?.parent)
        }
    }

    @Test
    fun `Given a local authority, when clear, then delete from realm`() {
        runTest {
            realm.write {
                copyToRealm(
                    StoredLocalAuthority().apply {
                        this.name = "name"
                        this.url = "url"
                        this.slug = "slug"
                        this.parent =
                            StoredLocalAuthorityParent().apply {
                                this.name = "parent name"
                                this.url = "parent url"
                                this.slug = "parent slug"
                            }
                    }
                )

                assertTrue(query<StoredLocalAuthority>().find().isNotEmpty())
            }

            val localDataSource = LocalDataSource(realmProvider)
            localDataSource.clear()

            assertTrue(realm.query<StoredLocalAuthority>().find().isEmpty())
        }
    }
}
