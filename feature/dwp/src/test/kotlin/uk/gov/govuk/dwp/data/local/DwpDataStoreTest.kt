package uk.gov.govuk.dwp.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.data.identity.model.ServiceLinkStatus

class DwpDataStoreTest {
    @Test
    fun `Given an empty store, then status is unlinked`() = runTest {
        val directory = createTempDirectory().toFile()
        try {
            val preferences = PreferenceDataStoreFactory.create(
                scope = backgroundScope,
                produceFile = { File(directory, "dwp.preferences_pb") }
            )
            assertEquals(ServiceLinkStatus.UNLINKED, DwpDataStore(preferences).linkState.first())
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `Given linked status, when cleared, then status is unlinked`() = runTest {
        val directory = createTempDirectory().toFile()
        try {
            val preferences = PreferenceDataStoreFactory.create(
                scope = backgroundScope,
                produceFile = { File(directory, "dwp.preferences_pb") }
            )
            val dataStore = DwpDataStore(preferences)
            dataStore.markLinked()
            assertEquals(ServiceLinkStatus.LINKED, dataStore.linkState.first())
            dataStore.clear()
            assertEquals(ServiceLinkStatus.UNLINKED, dataStore.linkState.first())
            assertTrue(preferences.data.first().asMap().isEmpty())
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `Given linked status, when the store is reopened, then status remains linked`() = runTest {
        val directory = createTempDirectory().toFile()
        val file = File(directory, "dwp.preferences_pb")
        val firstScope = CoroutineScope(coroutineContext + Job())
        val secondScope = CoroutineScope(coroutineContext + Job())
        try {
            val firstStore = PreferenceDataStoreFactory.create(scope = firstScope, produceFile = { file })
            DwpDataStore(firstStore).markLinked()
            firstScope.coroutineContext[Job]!!.cancel()
            firstScope.coroutineContext[Job]!!.join()

            val reopened = PreferenceDataStoreFactory.create(scope = secondScope, produceFile = { file })
            assertEquals(ServiceLinkStatus.LINKED, DwpDataStore(reopened).linkState.first())
            assertEquals(setOf("dwp_linked"), reopened.data.first().asMap().keys.map { it.name }.toSet())
        } finally {
            firstScope.cancel()
            secondScope.cancel()
            firstScope.coroutineContext[Job]!!.join()
            secondScope.coroutineContext[Job]!!.join()
            directory.deleteRecursively()
        }
    }
}
