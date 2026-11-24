package uk.gov.govuk.dvla.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import uk.gov.govuk.dvla.R

interface StringProvider {
    fun getString(
        @StringRes resId: Int,
        vararg formatArgs: Any
    ): String
    fun getQuantityString(
        @PluralsRes resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String
}

class StringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : StringProvider {
    override fun getString(
        resId: Int,
        vararg formatArgs: Any
    ): String = context.getString(resId, *formatArgs)

    override fun getQuantityString(
        resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String = context.resources.getQuantityString(resId, quantity, *formatArgs)
}

fun StringProvider.resolveSummaryDescription(
    @StringRes resId: Int?,
    dateArg: String?
): String {
    val id = resId ?: return getString(R.string.status_unknown) // TODO: return unknown for now, other states in future tickets
    return (dateArg?.let { getString(id, it) } ?: getString(id, "")).trim()
}

/**
 * Returns [todayResId] if [date] is today, otherwise [daysResId] resolved with the number of
 * days until [date]. Doesn't check whether [date] has passed
 */
fun StringProvider.resolveDaysUntil(
    date: LocalDate,
    @StringRes todayResId: Int,
    @PluralsRes daysResId: Int
): String {
    if (date.isToday()) return getString(todayResId)

    val days = date.getNumberOfDaysFromNow()
    return getQuantityString(daysResId, days, days)
}
