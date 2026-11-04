package uk.gov.govuk.topics.extension

import android.content.Context
import java.util.Locale
import uk.gov.govuk.topics.ui.model.TopicRef

/**
 * Maps a topic reference to a topic name and returns it.
 * If the reference cannot be mapped then the reference is formatted and returned.
 */
fun String.toTopicName(context: Context): String {
    val topicRef = TopicRef.fromString(this)

    return if (topicRef != null) {
        context.getString(topicRef.titleResId)
    } else {
        this.replace("-", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}
