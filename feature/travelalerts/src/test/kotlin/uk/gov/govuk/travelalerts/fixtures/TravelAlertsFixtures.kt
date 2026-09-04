package uk.gov.govuk.travelalerts.fixtures

import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group

object TravelAlertsFixtures {
    val mockGroups = listOf(
        Group(namespace = "ns1", group = "france", subgroup = "region1"),
        Group(namespace = "ns2", group = "germany", subgroup = "region2"),
        Group(namespace = "ns3", group = "spain", subgroup = "region3")
    )

    val mockCountries = listOf(
        Country(name = "France", slug = "france", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = emptyList()),
        Country(name = "Germany", slug = "germany", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = emptyList()),
        Country(name = "Spain", slug = "spain", rawLastUpdated = "2024-01-01T00:00:00Z", synonyms = emptyList())
    )
}
