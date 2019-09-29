package at.florianschuster.playables.main

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import at.florianschuster.playables.R
import kotlin.math.abs
import kotlin.math.max

class MainPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val alphaViews = listOfNotNull(
            page.findViewById<View?>(R.id.searchLayout),
            page.findViewById<View?>(R.id.searchScrollButton),
            page.findViewById<View?>(R.id.searchBottomShadowLayout)
        ) + listOfNotNull(
            page.findViewById<View?>(R.id.playablesScrollButton)
        )

        if (position in -1f..1f) {
            max(0f, 1 - (abs(position) * 5)).let { newAlpha ->
                alphaViews.forEach { it.alpha = newAlpha }
            }

            page.pivotY = page.height.toFloat()

            max(0.95f, 1 - abs(position)).let { scale ->
                page.scaleX = scale
                page.scaleY = scale
            }
        } else {
            alphaViews.forEach { it.alpha = 1f }

            with(page) {
                pivotY = height * 0.5f
                scaleX = 1f
                scaleY = 1f
            }
        }
    }
}