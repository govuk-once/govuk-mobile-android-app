package uk.gov.govuk.travelalerts.fixtures

import uk.gov.govuk.travelalerts.data.model.Group

object TravelAlertsFixtures {
    val mockGroups = listOf(
        Group(namespace = "ns1", group = "France", subgroup = "region1"),
        Group(namespace = "ns2", group = "Germany", subgroup = "region2"),
        Group(namespace = "ns3", group = "Spain", subgroup = "region3")
    )
}
