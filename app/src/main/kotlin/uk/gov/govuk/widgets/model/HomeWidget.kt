package uk.gov.govuk.widgets.model

import uk.gov.govuk.config.data.remote.model.ChatBanner
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.UserFeedbackBanner

internal sealed interface HomeWidget {
    data class Banner(val emergencyBanner: EmergencyBanner) : HomeWidget
    data class Chat(val chatBanner: ChatBanner) : HomeWidget
    data object Search : HomeWidget
    data object RecentActivity : HomeWidget
    data object Topics : HomeWidget
    data object Local : HomeWidget
    data class UserFeedback(val userFeedbackBanner: UserFeedbackBanner) : HomeWidget
}
