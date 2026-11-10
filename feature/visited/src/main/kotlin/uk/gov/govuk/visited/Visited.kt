package uk.gov.govuk.visited

interface Visited {

    suspend fun visitableItemClick(title: String, url: String)

    suspend fun clear()
}
