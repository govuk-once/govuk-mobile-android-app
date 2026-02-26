package uk.gov.govuk.design.ui.extension

import android.content.Context
import android.provider.Settings

fun Context.areAnimationsEnabled(): Boolean {
    val animatorDurationScale = Settings.Global.getFloat(
        this.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return animatorDurationScale > 0f
}
