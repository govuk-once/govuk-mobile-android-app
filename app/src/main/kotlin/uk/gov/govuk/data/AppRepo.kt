package uk.gov.govuk.data

import uk.gov.govuk.data.local.AppDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppRepo @Inject constructor(
    private val appDataStore: AppDataStore
) {
    internal suspend fun hasSkippedBiometrics() = appDataStore.hasSkippedBiometrics()

    internal suspend fun skipBiometrics() = appDataStore.skipBiometrics()

    internal suspend fun clearBiometricsSkipped() = appDataStore.clearBiometricsSkipped()

    internal suspend fun isTopicSelectionCompleted() = appDataStore.isTopicSelectionCompleted()

    internal suspend fun topicSelectionCompleted() = appDataStore.topicSelectionCompleted()

    internal val suppressedHomeWidgets = appDataStore.suppressedHomeWidgets

    internal suspend fun suppressHomeWidget(id: String) = appDataStore.suppressHomeWidget(id)

    internal suspend fun clear() = appDataStore.clear()
}
