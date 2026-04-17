package uk.gov.govuk.topics

import uk.gov.govuk.topics.ui.model.TopicUi

internal sealed interface TopicUiState {
    class Default(val topicUi: TopicUi) : TopicUiState
    class Offline(val topicReference: String) : TopicUiState
    class ServiceError(val topicReference: String) : TopicUiState
    class Loading(val topicReference: String) : TopicUiState
}
