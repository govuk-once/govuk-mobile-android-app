package uk.gov.govuk.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import uk.gov.govuk.analytics.data.AnalyticsRepo
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.analytics.extension.redactPii
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsClient @Inject constructor(
    private val analyticsRepo: AnalyticsRepo,
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient
) {

    lateinit var isUserSessionActive: () -> Boolean

    fun isAnalyticsConsentRequired(): Boolean {
        return analyticsRepo.analyticsEnabledState == AnalyticsEnabledState.NOT_SET
    }

    fun isAnalyticsEnabled(): Boolean {
        return analyticsRepo.analyticsEnabledState == AnalyticsEnabledState.ENABLED
    }

    suspend fun enable() {
        analyticsRepo.analyticsEnabled()
        firebaseAnalyticsClient.enable()
    }

    suspend fun disable() {
        analyticsRepo.analyticsDisabled()
        firebaseAnalyticsClient.disable()
    }

    suspend fun clear() {
        analyticsRepo.clear()
    }

    fun screenView(screenClass: String, screenName: String, title: String) {
        logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            parametersWithLanguage(
                mapOf(
                    FirebaseAnalytics.Param.SCREEN_CLASS to screenClass,
                    FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                    "screen_title" to title,
                )
            )
        )
    }

    fun screenViewWithType(
        screenClass: String,
        screenName: String,
        title: String,
        type: String
    ) {
        logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            parametersWithLanguage(
                mapOf(
                    FirebaseAnalytics.Param.SCREEN_CLASS to screenClass,
                    FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                    "screen_title" to title,
                    "type" to type
                )
            )
        )
    }

    fun buttonClick(
        text: String,
        url: String? = null,
        external: Boolean = false,
        section: String? = null
    ) {
        navigation(
            text = text,
            type = "Button",
            url = url,
            external = external,
            section = section
        )
    }

    fun tabClick(text: String) {
        navigation(text = text, type = "Tab")
    }

    fun widgetClick(
        text: String,
        url: String? = null,
        external: Boolean,
        section: String,
    ) {
        navigation(
            text = text,
            type = "Widget",
            url = url,
            external = external,
            section = section
        )
    }

    fun suppressWidgetClick(
        text: String,
        section: String
    ) {
        function(
            text = text,
            type = "Widget",
            section = section,
            action = "Remove"
        )
    }

    fun chat() {
        logEvent(
            "Chat",
            mapOf(
                "action" to "Ask Question",
                "type" to "typed"
            )
        )
    }

    fun search(searchTerm: String) {
        redactedEvent(name = "Search", type = "typed", inputString = searchTerm)
    }

    fun autocomplete(searchTerm: String) {
        redactedEvent(name = "Search", type = "autocomplete", inputString = searchTerm)
    }

    fun history(searchTerm: String) {
        redactedEvent(name = "Search", type = "history", inputString = searchTerm)
    }

    fun chatMarkdownLinkClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "ChatMarkdownLink", url = url, external = true)
    }

    fun chatQuestionAnswerReturnedEvent() {
        navigation(text = "Chat Question Answer Returned", type = "ChatQuestionAnswerReturned")
    }

    fun searchResultClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SearchResult", url = url, external = true)
    }

    fun visitedItemClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "VisitedItem", url = url, external = true)
    }

    fun settingsItemClick(text: String, url: String? = null, external: Boolean = true) {
        navigation(text = text, type = "SettingsItem", url = url, external = external)
    }

    fun toggleFunction(text: String, section: String, action: String) {
        function(
            text = text,
            type = "Toggle",
            section = section,
            action = action
        )
    }

    fun buttonFunction(
        text: String,
        section: String,
        action: String
    ) {
        function(
            text = text,
            type = "Button",
            section = section,
            action = action
        )
    }

    fun topicsCustomised() {
        firebaseAnalyticsClient.setUserProperty("topics_customised", "true")
    }

    fun selectItemEvent(ecommerceEvent: EcommerceEvent, selectedItemIndex: Int) {
        logEcommerceEvent(
            event = FirebaseAnalytics.Event.SELECT_ITEM,
            ecommerceEvent = ecommerceEvent,
            selectedItemIndex = selectedItemIndex
        )
    }

    fun viewItemListEvent(ecommerceEvent: EcommerceEvent) {
        logEcommerceEvent(
            event = FirebaseAnalytics.Event.VIEW_ITEM_LIST,
            ecommerceEvent = ecommerceEvent
        )
    }

    fun deepLinkEvent(hasDeepLink: Boolean, url: String) {
        navigation(
            text = if (hasDeepLink) "Opened" else "Failed",
            type = "DeepLink",
            url = url
        )
    }

    fun logException(exception: Exception) {
        firebaseAnalyticsClient.logException(exception)
    }

    private fun redactedEvent(name: String, type: String, inputString: String) {
        logEvent(
            name,
            mapOf(
                "type" to type,
                "text" to inputString.redactPii()
            )
        )
    }

    private fun navigation(
        text: String? = null,
        type: String,
        url: String? = null,
        external: Boolean = false,
        section: String? = null
    ) {
        val parameters = mutableMapOf(
            "type" to type,
            "external" to external,
        )

        text?.let {
            parameters["text"] = it
        }

        url?.let {
            parameters["url"] = it
        }

        section?.let {
            parameters["section"] = it
        }

        logEvent("Navigation", parametersWithLanguage(parameters))
    }

    private fun function(text: String, type: String, section: String, action: String) {
        val parameters = mapOf(
            "text" to text,
            "type" to type,
            "section" to section,
            "action" to action
        )

        logEvent("Function", parametersWithLanguage(parameters))
    }

    private fun parametersWithLanguage(parameters: Map<String, Any>): Map<String, Any> {
        return parameters + Pair("language", Locale.getDefault().language)
    }

    private fun logEvent(name: String, parameters: Map<String, Any>) {
        if (isAnalyticsEnabled() && isUserSessionActive()) {
            firebaseAnalyticsClient.logEvent(name, parameters)
        }
    }

    private fun logEcommerceEvent(event: String, ecommerceEvent: EcommerceEvent, selectedItemIndex: Int? = null) {
        if (isAnalyticsEnabled() && isUserSessionActive()) {
            firebaseAnalyticsClient.logEcommerceEvent(event, ecommerceEvent, selectedItemIndex)
        }
    }
}
