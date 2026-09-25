package uk.gov.govuk.travelalerts.fixtures

import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group

object TravelAlertsFixtures {
    val mockGroups = listOf(
        Group(namespace = "travel", group = "france", subgroup = "instant"),
        Group(namespace = "travel", group = "germany", subgroup = "instant"),
        Group(namespace = "travel", group = "spain", subgroup = "instant")
    )

    val mockCountries = listOf(
        Country(name = "France", slug = "france", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = emptyList()),
        Country(name = "Germany", slug = "germany", rawLastUpdated = "2025-01-01T00:00:00Z", synonyms = emptyList()),
        Country(name = "Spain", slug = "spain", rawLastUpdated = "2024-06-01T00:00:00Z", synonyms = emptyList())
    )
}
