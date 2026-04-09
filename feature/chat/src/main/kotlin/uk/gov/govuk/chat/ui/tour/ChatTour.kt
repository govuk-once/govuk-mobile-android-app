package uk.gov.govuk.chat.ui.tour

import uk.gov.govuk.tour.TourConfig
import uk.gov.govuk.tour.TourStep

internal const val TOUR_TARGET_HEADER = "chat_header"
internal const val TOUR_TARGET_INTRO = "chat_intro"
internal const val TOUR_TARGET_INPUT = "chat_input"
internal const val TOUR_TARGET_MENU = "chat_menu"

internal val chatTourConfig = TourConfig(
    id = "chat",
    steps = listOf(
        TourStep(
            targetKey = TOUR_TARGET_HEADER,
            title = "GOV.UK Chat",
            body = "Ask questions about government services and get answers drawn from GOV.UK content."
        ),
        TourStep(
            targetKey = TOUR_TARGET_INTRO,
            title = "Your answers",
            body = "GOV.UK Chat's responses appear here. This example shows what a typical answer looks like."
        ),
        TourStep(
            targetKey = TOUR_TARGET_INPUT,
            title = "Ask a question",
            body = "Type your question here. You can enter up to 300 characters."
        ),
        TourStep(
            targetKey = TOUR_TARGET_MENU,
            title = "More options",
            body = "Access help, the privacy policy, feedback, and conversation controls."
        )
    )
)
