package at.florianschuster.playables.core.android.local

import android.content.SharedPreferences
import androidx.core.content.edit
import at.florianschuster.playables.core.local.Settings
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class SharedPrefsSettings(
    private val prefs: SharedPreferences
) : Settings {

    private enum class Keys { OnboardingDate, RatedDate, AnalyticsEnabled }

    override var onboardingShownDate: LocalDateTime
        get() = prefs.getLong(Keys.OnboardingDate.name, 0).toLocalDateTime()
        set(value) = prefs.edit(commit = true) {
            putLong(Keys.OnboardingDate.name, value.toMillis())
        }

    override var ratedDate: LocalDateTime
        get() = prefs.getLong(Keys.RatedDate.name, 0).toLocalDateTime()
        set(value)  = prefs.edit(commit = true) {
            putLong(Keys.RatedDate.name, value.toMillis())
        }

    override var analyticsEnabled: Boolean
        get() = prefs.getBoolean(Keys.AnalyticsEnabled.name, true)
        set(value) = prefs.edit(commit = true) {
            putBoolean(Keys.AnalyticsEnabled.name, value)
        }

    private fun LocalDateTime.toMillis(): Long =
        atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

    private fun Long.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneOffset.UTC)
}