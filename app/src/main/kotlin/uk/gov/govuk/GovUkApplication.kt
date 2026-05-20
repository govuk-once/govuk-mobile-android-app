package uk.gov.govuk

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import uk.gov.govuk.analytics.QualtricsAnalyticsClient
import uk.gov.govuk.notifications.NotificationsProvider
import javax.inject.Inject

@HiltAndroidApp
class GovUkApplication: Application() {

    @Inject lateinit var notificationsProvider: NotificationsProvider
    @Inject lateinit var qualtricsAnalyticsClient: QualtricsAnalyticsClient

    override fun onCreate() {
        super.onCreate()
        notificationsProvider.initialise(BuildConfig.ONE_SIGNAL_APP_ID)
        notificationsProvider.addClickListener()

        qualtricsAnalyticsClient.initialize()
    }
}
