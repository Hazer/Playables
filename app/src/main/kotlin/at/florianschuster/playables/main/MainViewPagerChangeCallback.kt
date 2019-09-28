package at.florianschuster.playables.main

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.viewpager2.widget.ViewPager2

class MainViewPagerChangeCallback(
    private val headerMotionLayout: MotionLayout
) : ViewPager2.OnPageChangeCallback() {
    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        headerMotionLayout.progress = (position + positionOffset) / (MainPages.values().size - 1)
    }

    override fun onPageSelected(position: Int) {
    }
}