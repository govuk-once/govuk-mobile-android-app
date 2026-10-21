package uk.gov.govuk.analytics.data.local.model

enum class AbTestEvent(val eventName: String) {
    // TODO - Android A/A test 10-26 - remove when the test has finished
    HOME_ACTIVATION_10_26("ab_test_activation_home_10_26"),
    SEARCH_CONVERSION_10_26("ab_test_conversion_search_invoked_10_26")
}
