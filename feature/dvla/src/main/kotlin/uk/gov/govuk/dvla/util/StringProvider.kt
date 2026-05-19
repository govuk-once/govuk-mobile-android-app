package uk.gov.govuk.dvla.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface StringProvider {
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

class StringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : StringProvider {
    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}