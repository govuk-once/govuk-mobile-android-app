package uk.gov.govuk.search.domain

object SearchConfig {
    const val BASE_URL = "https://www.gov.uk"

//    Search API V1 ===>
//    val API_BASE_URL = "https://www.gov.uk"
//    const val SEARCH_PATH = "/api/search.json"
//    const val DESCRIPTION_RESPONSE_FIELD = "description"

    //    Search API V2 ===>
    const val API_BASE_URL = "https://search.publishing.service.gov.uk"
    const val SEARCH_PATH = "/v0_1/search.json"
    //    field has both changed name and is now optional!
    const val DESCRIPTION_RESPONSE_FIELD = "description_with_highlighting"


//    Autocomplete API ===>
    const val AUTOCOMPLETE_PATH = "/api/search/autocomplete.json"

    const val DEFAULT_RESULTS_PER_PAGE = 10

    const val MAX_PREVIOUS_SEARCH_COUNT = 5

    const val AUTOCOMPLETE_MIN_LENGTH = 3
}
