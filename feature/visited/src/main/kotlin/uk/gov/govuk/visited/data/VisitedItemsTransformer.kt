package uk.gov.govuk.visited.data

import uk.gov.govuk.visited.SectionTitles
import uk.gov.govuk.visited.domain.model.VisitedItemUi
import uk.gov.govuk.visited.ui.model.VisitedUi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

fun transformVisitedItems(visitedItems: List<VisitedItemUi>, todaysDate: LocalDate): Map<String, List<VisitedUi>> {
    fun VisitedItemUi.toLocalDate() = LocalDateTime.ofEpochSecond(this.lastVisited, 0, ZoneOffset.UTC).toLocalDate()

    val todaysItems = visitedItems.filter { it.toLocalDate().isEqual(todaysDate) }
    val thisMonthsItems = visitedItems.filter {
        it.toLocalDate().month == todaysDate.month &&
            it.toLocalDate().year == todaysDate.year &&
            it.toLocalDate().dayOfMonth != todaysDate.dayOfMonth
    }
    val previousMonthsItems = visitedItems.filter {
        it.toLocalDate().month != todaysDate.month ||
            it.toLocalDate().year != todaysDate.year
    }

    fun VisitedItemUi.previousMonthKey() = "${capitaliseMonth(toLocalDate().month.name)} ${toLocalDate().year}"
    val groupedPreviousMonthsItems = previousMonthsItems.groupBy { it.previousMonthKey() }

    fun toVisitedUi(items: List<VisitedItemUi>): List<VisitedUi> = items.map {
        VisitedUi(
            title = it.title,
            url = it.url,
            lastVisited = localDateFormatter(it.lastVisited)
        )
    }

    val groupedVisitedItems = mutableMapOf<String, List<VisitedUi>>()

    if (todaysItems.isNotEmpty()) {
        groupedVisitedItems[SectionTitles().today] = toVisitedUi(todaysItems)
    }

    if (thisMonthsItems.isNotEmpty()) {
        groupedVisitedItems[SectionTitles().thisMonth] = toVisitedUi(thisMonthsItems)
    }

    groupedPreviousMonthsItems.forEach { (key, value) ->
        groupedVisitedItems[key] = toVisitedUi(value)
    }

    return groupedVisitedItems
}
