package at.florianschuster.playables.util

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import at.florianschuster.playables.R
import com.tailoredapps.androidutil.ui.IntentUtil
import timber.log.Timber

fun FragmentActivity.openChromeTab(
    url: String
) {
    if (url.isEmpty()) return
    try {
        val tabsIntent = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(this@openChromeTab, R.color.colorPrimary))
            setShowTitle(true)
            enableUrlBarHiding()
        }.build()
        tabsIntent.launchUrl(this, Uri.parse(url))
    } catch (throwable: Throwable) {
        Timber.w(throwable)
        try {
            startActivity(IntentUtil.web(url)) // fallback of chrome not installed -> open default browser
        } catch (anotherOne: Throwable) {
            Timber.w(anotherOne)
        }
    }
}