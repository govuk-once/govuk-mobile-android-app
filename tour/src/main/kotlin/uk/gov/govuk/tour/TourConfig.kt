package uk.gov.govuk.tour

data class TourConfig(
    val id: String,
    val steps: List<TourStep>
)
