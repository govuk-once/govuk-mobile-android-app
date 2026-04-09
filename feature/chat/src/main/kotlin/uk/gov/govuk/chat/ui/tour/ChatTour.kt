package uk.gov.govuk.chat.ui.tour

import uk.gov.govuk.chat.R
import uk.gov.govuk.tour.TourConfig
import uk.gov.govuk.tour.TourStep

internal val chatTourConfig = TourConfig(
    id = "chat",
    steps = listOf(
        TourStep(
            targetKey = "chat_header",
            title = "GOV.UK Chat",
            body = "Ask questions about government services and get answers drawn from GOV.UK content.",
            illustrationRes = R.drawable.onboarding_page_one
        ),
        TourStep(
            targetKey = "chat_input",
            title = "Ask a question",
            body = "Type your question here. You can enter up to 300 characters.",
            illustrationRes = R.drawable.onboarding_page_two
        ),
        TourStep(
            targetKey = "chat_menu",
            title = "More options",
            body = "Access help, the privacy policy, feedback, and conversation controls.",
            illustrationRes = R.drawable.ic_tour_more_options
        )
    )
)
