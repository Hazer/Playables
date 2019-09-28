package at.florianschuster.playables.main

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import at.florianschuster.playables.R
import kotlin.math.abs
import kotlin.math.max

class MainPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val searchLayout = page.findViewById<View?>(R.id.searchLayout)
        val searchScrollButton = page.findViewById<View?>(R.id.searchScrollButton)

        if (position in -1f..1f) {
            max(0f, 1 - (abs(position) * 5)).let {
                searchLayout?.alpha = it
                searchScrollButton?.alpha = it
            }

            max(0.9f, 1 - abs(position)).let {
                page.scaleX = it
                page.scaleY = it
            }

            page.translationY = 0f
        } else {
            searchLayout?.alpha = 1f
            searchScrollButton?.alpha = 1f

            page.scaleX = 1f
            page.scaleY = 1f

            page.translationY = 0f
        }
    }
}