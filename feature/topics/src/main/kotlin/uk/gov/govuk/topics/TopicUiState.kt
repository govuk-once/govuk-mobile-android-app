package uk.gov.govuk.topics

import uk.gov.govuk.topics.ui.model.TopicUi

internal sealed class TopicUiState {
    // /TODO showDvlaLink flag added for POC
    internal class Default(val topicUi: TopicUi, val showDvlaLink: Boolean = false) : TopicUiState()
    internal class Offline(val topicReference: String) : TopicUiState()
    internal class ServiceError(val topicReference: String) : TopicUiState()
}
