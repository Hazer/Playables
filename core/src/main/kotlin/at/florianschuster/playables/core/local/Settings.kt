package at.florianschuster.playables.core.local

import java.time.LocalDateTime

interface Settings {
    var onboardingShownDate: LocalDateTime
    var ratedDate: LocalDateTime
    var analyticsEnabled: Boolean
}