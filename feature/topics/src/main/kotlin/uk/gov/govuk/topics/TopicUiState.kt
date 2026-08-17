package uk.gov.govuk.topics

import uk.gov.govuk.topics.ui.model.TopicUi

internal sealed interface TopicUiState {
    data class Default(val topicUi: TopicUi) : TopicUiState
    data class Loading(val topicReference: String) : TopicUiState
    sealed interface Error : TopicUiState {
        object NoReference : Error
        data class Offline(val topicReference: String) : Error
        data class Service(val topicReference: String) : Error
    }
}
