package uk.gov.govuk.travelalerts.data

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.travelalerts.data.model.Group
import java.time.Instant

interface DateProvider {
    val date: Instant
}

interface TravelAlertsRepo {
    suspend fun getGroups(): Result<List<Group>>
}