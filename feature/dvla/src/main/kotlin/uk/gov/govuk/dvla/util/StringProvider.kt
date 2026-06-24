package uk.gov.govuk.dvla.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface StringProvider {
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
    fun getPlural(@PluralsRes resId: Int, quantity: Int): String
}

class StringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : StringProvider {
    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }

    override fun getPlural(resId: Int, quantity: Int): String {
        return context.resources.getQuantityString(resId, quantity, quantity)
    }
}

fun StringProvider.resolveSummaryDescription(@StringRes resId: Int?, dateArg: String?): String {
    val id = resId ?: return "Unknown" // TODO: return unknown for now, other states in future tickets
    return (dateArg?.let { getString(id, it) } ?: getString(id, "")).trim()
}
