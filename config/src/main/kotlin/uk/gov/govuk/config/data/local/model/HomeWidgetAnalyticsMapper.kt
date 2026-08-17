package uk.gov.govuk.config.data.local.model

import uk.gov.govuk.analytics.data.local.model.EcommerceEvent.Item

const val HOME_BANNERS = "home_banners"

fun HomeWidget.toAnalyticsItem() : Item? {
    return when (this) {
        is HomeWidget.Banner -> Item(
            itemId = emergencyBanner.id,
            itemName = emergencyBanner.title ?: "",
            itemCategory = emergencyBanner.javaClass.simpleName,
            locationId = emergencyBanner.link?.url ?: ""
        )
        is HomeWidget.Chat -> Item(
            itemId = chatBanner.id,
            itemName = chatBanner.title,
            itemCategory = chatBanner.javaClass.simpleName,
            locationId = chatBanner.link.url
        )
        is HomeWidget.Promo -> Item(
            itemId = promoBanner.id,
            itemName = promoBanner.title,
            itemCategory = promoBanner.javaClass.simpleName,
            locationId = promoBanner.link.url
        )
        is HomeWidget.UserFeedback -> Item(
            itemName = userFeedbackBanner.link.title,
            itemCategory = userFeedbackBanner.javaClass.simpleName,
            locationId = userFeedbackBanner.link.url
        )
        else -> null
    }
}

fun List<HomeWidget>.toAnalyticsItems() : List<Item> {
    return this.mapNotNull { it.toAnalyticsItem() }
}
