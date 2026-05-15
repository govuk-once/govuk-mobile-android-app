package uk.gov.govuk

import android.app.Application
import com.qualtrics.digital.Qualtrics
import dagger.hilt.android.HiltAndroidApp
import uk.gov.govuk.notifications.NotificationsProvider
import javax.inject.Inject

@HiltAndroidApp
class GovUkApplication: Application() {

    @Inject lateinit var notificationsProvider: NotificationsProvider
    @Inject lateinit var qualtricsProvider: Qualtrics

    override fun onCreate() {
        super.onCreate()
        notificationsProvider.initialise(BuildConfig.ONE_SIGNAL_APP_ID)
        notificationsProvider.addClickListener()

        qualtricsProvider.initializeProject(BuildConfig.QUALTRICS_BRAND_ID, BuildConfig.QUALTRICS_PROJECT_ID, this)
    }
}
