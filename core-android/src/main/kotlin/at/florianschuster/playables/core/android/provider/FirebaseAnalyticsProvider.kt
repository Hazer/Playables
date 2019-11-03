package at.florianschuster.playables.core.android.provider

import at.florianschuster.playables.core.local.Settings
import at.florianschuster.playables.core.provider.AnalyticsProvider
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsProvider(
    private val analytics: FirebaseAnalytics,
    private val settings: Settings
) : AnalyticsProvider {
    override var enabled: Boolean
        get() = settings.analyticsEnabled
        set(value) {
            settings.analyticsEnabled = value
            analytics.setAnalyticsCollectionEnabled(value)
        }
}