package uk.gov.govuk.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.qualtrics.digital.Qualtrics
import com.qualtrics.digital.QualtricsTheme
import com.qualtrics.digital.theming.embedded.EmbeddedAppFeedbackTheme
import com.qualtrics.digital.theming.embedded.FollowupQuestionTheme
import com.qualtrics.digital.theming.embedded.InitialQuestionTheme
import com.qualtrics.digital.theming.embedded.MultipleChoiceTheme
import com.qualtrics.digital.theming.embedded.RadioButtonsTheme
import com.qualtrics.digital.theming.embedded.SubmitButtonTheme
import com.qualtrics.digital.theming.embedded.ThankYouTheme
import com.qualtrics.digital.theming.embedded.response.EmojiTheme
import com.qualtrics.digital.theming.embedded.response.StarTheme
import com.qualtrics.digital.theming.embedded.response.TextInputTheme
import com.qualtrics.digital.theming.embedded.response.ThumbsButtonsTheme
import com.qualtrics.digital.theming.embedded.response.YesNoButtonsTheme
import com.qualtrics.digital.theming.fonts.FontTheme
import com.qualtrics.digital.theming.prompt.ButtonTheme
import com.qualtrics.digital.theming.prompt.MobileAppPromptTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import javax.inject.Inject

class QualtricsAnalyticsClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val qualtrics: Qualtrics,
    private val activityProvider: ActivityProviderInterface
) {

    internal var isInitialized = false

    fun initialize() {
        if (isInitialized) return

        qualtrics.initializeProject(
            BuildConfig.QUALTRICS_BRAND_ID,
            BuildConfig.QUALTRICS_PROJECT_ID,
            context
        )

        qualtrics.creativeTheme = qualtricsTheme()

        isInitialized = true
    }

    fun logEvent(eventName: String, parameters: Map<String, Any>) {
        setParameters(parameters)

        registerVisitAndEvaluateForTriggers(eventName)
    }

    fun logEcommerceEvent(eventName: String, ecommerceEvent: EcommerceEvent) {
        setParameters(
            mapOf(
                FirebaseAnalytics.Param.ITEM_LIST_ID to ecommerceEvent.itemListId,
                FirebaseAnalytics.Param.ITEM_LIST_NAME to ecommerceEvent.itemListName
            )
        )

        registerVisitAndEvaluateForTriggers(eventName)
    }

    private fun registerVisitAndEvaluateForTriggers(eventName: String) {
        qualtrics.registerViewVisit(eventName)

        qualtrics.evaluateProject { results ->
            if (results.values.any { it.passed() }) {
                activityProvider.currentActivity?.let { activity ->
                    qualtrics.display(activity)
                }
            }
        }
    }

    /**
     * The Qualtrics SDK data storage mechanism is implemented as a single (flat) map
     * that is cached across events. So, it does not handle objects for sending
     * data - specifically maps, arrays of maps and nested arrays. It also means we
     * need to flat-map all the keys and values we would ever want to send - making
     * the keys unique in some way - for example, e-commerce events that have 'items'.
     *
     * As a consequence of the above - if a key is not overwritten in newer events
     * - it will be resent in subsequent events, causing incorrect event data to
     * be 'leaked' across events.
     *
     * This seems to be a deliberate 'feature'.
     *
     * To date, the only solution to this seems to be creating a unique, defined and
     * distinct set of keys that are set to the new events value or an empty string
     * before being sent. It seems this is the only way to ensure that only valid
     * data is sent.
     */
    private val analyticsParameterKeys = listOf(
        "action", "external", "item_list_id", "item_list_name",
        "language", "screen_class", "screen_name", "screen_title",
        "section", "text", "type", "url"
    )

    private fun setParameters(parameters: Map<String, Any>) {
        analyticsParameterKeys.forEach { key ->
            qualtrics.properties.setString(key, parameters[key]?.toString() ?: "")
        }
    }

    private fun qualtricsTheme(): QualtricsTheme {
        // TODO: Qualtrics SDK v3 - Remove XML color bridge and use .toArgb() directly
        // TODO: Example: GovUkTheme.colourScheme.surfaces.background.toArgb()

        // TODO: Similar for fonts...
        val bodyRegular = R.font.transport_light
        val bodyBold = R.font.transport_bold
        val regularSize = 17
        val mediumSize = 22
        val largeSize = 28

        return QualtricsTheme.Builder()
            .setMobileAppPromptTheme(
                MobileAppPromptTheme(
                    backgroundColor = R.color.background,
                    headlineTextColor = R.color.text,
                    headlineFont = FontTheme(bodyBold, largeSize),
                    descriptionTextColor = R.color.text,
                    descriptionFont = FontTheme(bodyRegular, regularSize),
                    closeButtonColor = R.color.background,
                    closeButtonBackgroundColor = R.color.primary,
                    buttonOneTheme = ButtonTheme(
                        labelColor = R.color.primary,
                        font = FontTheme(bodyRegular, mediumSize),
                        backgroundColor = R.color.background,
                        borderColor = R.color.primary,
                        linkColor = R.color.link
                    ),
                    buttonTwoTheme = ButtonTheme(
                        labelColor = R.color.background,
                        font = FontTheme(bodyRegular, mediumSize),
                        backgroundColor = R.color.primary,
                        borderColor = R.color.primary,
                        linkColor = R.color.link
                    )
                )
            )
            .setEmbeddedAppFeedbackTheme(
                EmbeddedAppFeedbackTheme(
                    dialogBackgroundColor = R.color.background,
                    closeButtonColor = R.color.background,
                    closeButtonBackgroundColor = R.color.primary,
                    initialQuestionTheme = InitialQuestionTheme(
                        color = R.color.text,
                        initialQuestion = FontTheme(bodyBold, largeSize)
                    ),
                    followupQuestionTheme = FollowupQuestionTheme(
                        color = R.color.text,
                        followupQuestionFont = FontTheme(bodyBold, largeSize),
                        followupQuestionTextInputFont = FontTheme(bodyRegular, regularSize)
                    ),
                    thankYouTheme = ThankYouTheme(
                        color = R.color.text,
                        thankYouTextFont = FontTheme(bodyBold, largeSize),
                    ),
                    yesNoButtonsTheme = YesNoButtonsTheme(
                        yesButtonTextColor = R.color.background,
                        yesButtonBorderColor = R.color.primary,
                        yesButtonFillColor = R.color.primary,
                        yesButtonFont = FontTheme(bodyRegular, mediumSize),
                        noButtonTextColor = R.color.primary,
                        noButtonBorderColor = R.color.primary,
                        noButtonFillColor = R.color.background,
                        noButtonFont = FontTheme(bodyRegular, mediumSize),
                    ),
                    thumbsButtonsTheme = ThumbsButtonsTheme(
                        thumbUpBorderColor = R.color.text,
                        thumbUpFillColor = R.color.secondary,
                        thumbDownBorderColor = R.color.text,
                        thumbDownFillColor = R.color.secondary
                    ),
                    emojiTheme = EmojiTheme(
                        borderColor = R.color.primary,
                        fillColor = R.color.primary,
                        tintColor = R.color.background
                    ),
                    starTheme = StarTheme(
                        borderColor = R.color.primary
                    ),
                    multipleChoiceTheme = MultipleChoiceTheme(
                        questionTextFont = FontTheme(bodyRegular, regularSize),
                        otherAnswerTextColor = R.color.background,
                        otherAnswerTextFont = FontTheme(bodyRegular, regularSize),
                        otherAnswerBackgroundColor = R.color.secondary,
                        radioButtonsTheme = RadioButtonsTheme(
                            textFont = FontTheme(bodyRegular, regularSize),
                            selectedCircleColor = R.color.background,
                            selectedBackgroundColor = R.color.secondary,
                            unselectedCircleColor = R.color.secondary,
                        )
                    ),
                    submitButtonTheme = SubmitButtonTheme(
                        textColor = R.color.background,
                        fillColor = R.color.primary,
                        font = FontTheme(bodyRegular, mediumSize)
                    ),
                    textInputTheme = TextInputTheme(
                        multilineTextInputColor = R.color.background,
                        multilineTextInputBackgroundColor = R.color.secondary,
                    )
                )
            )
            .build()
    }
}
