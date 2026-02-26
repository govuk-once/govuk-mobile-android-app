package uk.gov.govuk.topics

import uk.gov.govuk.topics.ui.model.TopicUi

internal sealed class TopicUiState {
    internal class Default(val topicUi: TopicUi) : TopicUiState()

    internal class Offline(val topicReference: String) : TopicUiState()

    internal class ServiceError(val topicReference: String) : TopicUiState()
}
