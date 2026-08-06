package uk.gov.govuk.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.qualtrics.digital.Qualtrics
import com.qualtrics.digital.QualtricsPopOverActivity
import com.qualtrics.digital.QualtricsSurveyActivity
import com.qualtrics.digital.QualtricsTheme
import com.qualtrics.digital.TargetingResult
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
    private val firebaseIdentifiers: FirebaseIdentifiers,
    // Dagger will read the wrong object unless the interface - not instance - is specified here
    private val activityProvider: ActivityProviderInterface
) {

    internal var isInitialized = false

    private var onSurveyClosedListener: ((targetingIds: List<String>) -> Unit)? = null
    private var lastShownTargetingIds: List<String> = emptyList()

    fun initialize() {
        if (isInitialized) return

        qualtrics.initializeProject(
            BuildConfig.QUALTRICS_BRAND_ID,
            BuildConfig.QUALTRICS_PROJECT_ID,
            context
        )

        qualtrics.creativeTheme = qualtricsTheme()

        activityProvider.addOnActivityDestroyedListener { activity ->
            if (activity is QualtricsPopOverActivity || activity is QualtricsSurveyActivity) {
                onSurveyClosedListener?.invoke(lastShownTargetingIds)
            }
        }

        isInitialized = true
    }

    fun setOnSurveyClosedListener(listener: (targetingIds: List<String>) -> Unit) {
        onSurveyClosedListener = listener
    }

    fun logEvent(
        eventName: String,
        parameters: Map<String, Any>,
        onSurveyShown: ((results: Map<String, TargetingResult>) -> Unit)? = null
    ) {
        setParameters(parameters)

        registerVisitAndEvaluateForTriggers(eventName, onSurveyShown)
    }

    fun logEcommerceEvent(
        eventName: String,
        ecommerceEvent: EcommerceEvent,
        onSurveyShown: ((results: Map<String, TargetingResult>) -> Unit)? = null
    ) {
        setParameters(
            mapOf(
                FirebaseAnalytics.Param.ITEM_LIST_ID to ecommerceEvent.itemListId,
                FirebaseAnalytics.Param.ITEM_LIST_NAME to ecommerceEvent.itemListName
            )
        )

        registerVisitAndEvaluateForTriggers(eventName, onSurveyShown)
    }

    private fun registerVisitAndEvaluateForTriggers(
        eventName: String,
        onSurveyShown: ((results: Map<String, TargetingResult>) -> Unit)? = null
    ) {
        qualtrics.registerViewVisit(eventName)

        qualtrics.evaluateProject { results ->
            val passedTargetingIds = results.filterValues {
                it.passed()
            }.keys.toList()

            if (passedTargetingIds.isNotEmpty()) {
                activityProvider.currentActivity?.let { activity ->
                    lastShownTargetingIds = passedTargetingIds
                    qualtrics.display(activity)
                    onSurveyShown?.invoke(results)
                }
            }
        }
    }

    /**
     * The Qualtrics SDK data storage mechanism is implemented as a single (flat) map
     * that is cached across events. It does not handle objects for sending
     * data - specifically maps, arrays of maps and nested arrays.
     *
     * This means we need to flat-map all the keys and values we would ever want to
     * send - making the keys unique in some way - for example, e-commerce events
     * that have 'items'  might need the index in the key.
     *
     * As a consequence of the above - if a key is not overwritten in newer events
     * - old stale values will be resent in subsequent events, causing incorrect
     * event data to be 'leaked' across events.
     */
    private val analyticsParameterKeys = listOf(
        "action", "external", "item_list_id", "item_list_name",
        "language", "qualtrics_targeting_id", "screen_class", "screen_name",
        "screen_title", "section", "text", "type", "url"
    )

    private fun setParameters(parameters: Map<String, Any>) {
        firebaseIdentifiers.userPseudoId?.let {
            qualtrics.properties.setString(FIREBASE_USER_PSEUDO_ID, it)
        }

        firebaseIdentifiers.sessionId?.let {
            qualtrics.properties.setString(FIREBASE_SESSION_ID, it)
        }

        analyticsParameterKeys.forEach { key ->
            qualtrics.properties.setString(key, parameters[key]?.toString() ?: "")
        }

        firebaseIdentifiers.refresh()
    }

    private fun qualtricsTheme(): QualtricsTheme {
        // In Qualtrics SDK v3, we should be able to do the following:
        //   - Remove XML color bridge and use .toArgb() directly
        //       Example: GovUkTheme.colourScheme.surfaces.background.toArgb()
        //   - Similar process for fonts

        val bodyRegular = R.font.transport_light
        val bodyBold = R.font.transport_bold
        val regularSize = 17
        val mediumSize = 22
        val largeSize = 28

        return QualtricsTheme.Builder()
            .setMobileAppPromptTheme(
                MobileAppPromptTheme(
                    backgroundColor = R.color.surface_list,
                    headlineTextColor = R.color.text_primary,
                    headlineFont = FontTheme(bodyBold, largeSize),
                    descriptionTextColor = R.color.text_primary,
                    descriptionFont = FontTheme(bodyRegular, regularSize),
                    closeButtonColor = R.color.white,
                    closeButtonBackgroundColor = R.color.accent,
                    buttonOneTheme = ButtonTheme(
                        labelColor = R.color.accent,
                        font = FontTheme(bodyRegular, mediumSize),
                        backgroundColor = R.color.surface_list,
                        borderColor = R.color.accent,
                        linkColor = R.color.accent
                    ),
                    buttonTwoTheme = ButtonTheme(
                        labelColor = R.color.white,
                        font = FontTheme(bodyRegular, mediumSize),
                        backgroundColor = R.color.accent,
                        borderColor = R.color.accent,
                        linkColor = R.color.accent
                    )
                )
            )
            .setEmbeddedAppFeedbackTheme(
                EmbeddedAppFeedbackTheme(
                    dialogBackgroundColor = R.color.surface_list,
                    closeButtonColor = R.color.white,
                    closeButtonBackgroundColor = R.color.accent,
                    initialQuestionTheme = InitialQuestionTheme(
                        color = R.color.text_primary,
                        initialQuestion = FontTheme(bodyBold, largeSize)
                    ),
                    followupQuestionTheme = FollowupQuestionTheme(
                        color = R.color.text_primary,
                        followupQuestionFont = FontTheme(bodyBold, largeSize),
                        followupQuestionTextInputFont = FontTheme(bodyRegular, regularSize)
                    ),
                    thankYouTheme = ThankYouTheme(
                        color = R.color.text_primary,
                        thankYouTextFont = FontTheme(bodyBold, largeSize),
                    ),
                    yesNoButtonsTheme = YesNoButtonsTheme(
                        yesButtonTextColor = R.color.accent,
                        yesButtonBorderColor = R.color.white,
                        yesButtonFillColor = R.color.accent,
                        yesButtonFont = FontTheme(bodyRegular, mediumSize),
                        noButtonTextColor = R.color.accent,
                        noButtonBorderColor = R.color.accent,
                        noButtonFillColor = R.color.surface_list,
                        noButtonFont = FontTheme(bodyRegular, mediumSize),
                    ),
                    thumbsButtonsTheme = ThumbsButtonsTheme(
                        thumbUpBorderColor = R.color.accent,
                        thumbUpFillColor = R.color.surface_list,
                        thumbDownBorderColor = R.color.accent,
                        thumbDownFillColor = R.color.surface_list
                    ),
                    emojiTheme = EmojiTheme(
                        borderColor = R.color.accent,
                        fillColor = R.color.accent,
                        tintColor = R.color.white
                    ),
                    starTheme = StarTheme(
                        borderColor = R.color.accent
                    ),
                    multipleChoiceTheme = MultipleChoiceTheme(
                        questionTextFont = FontTheme(bodyRegular, regularSize),
                        otherAnswerTextColor = R.color.text_primary,
                        otherAnswerTextFont = FontTheme(bodyRegular, regularSize),
                        otherAnswerBackgroundColor = R.color.surface_background,
                        radioButtonsTheme = RadioButtonsTheme(
                            textFont = FontTheme(bodyRegular, regularSize),
                            selectedCircleColor = R.color.accent_light,
                            selectedBackgroundColor = R.color.surface_list,
                            unselectedCircleColor = R.color.list_divider,
                        )
                    ),
                    submitButtonTheme = SubmitButtonTheme(
                        textColor = R.color.white,
                        fillColor = R.color.accent,
                        font = FontTheme(bodyRegular, mediumSize)
                    ),
                    textInputTheme = TextInputTheme(
                        multilineTextInputColor = R.color.text_primary,
                        multilineTextInputBackgroundColor = R.color.surface_background,
                    )
                )
            )
            .build()
    }
}
