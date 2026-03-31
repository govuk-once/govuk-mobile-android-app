package uk.gov.govuk.topics

// TODO (TECH DEBT) DVLA POC
// This enum temporarily couples the Topic module to DVLA
// Once tech debt in TopicViewModel is sorted move this enum out of the topics
// and into the Dvla (or app) module
enum class DvlaLinkState {
    CHECKING,
    UNLINKED,
    LINKED
}