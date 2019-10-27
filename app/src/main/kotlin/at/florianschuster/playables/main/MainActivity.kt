package at.florianschuster.playables.main

import android.os.Bundle
import android.view.View.*
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import at.florianschuster.playables.R
import at.florianschuster.playables.base.BaseActivity
import at.florianschuster.playables.info.showInfoBottomSheet
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_header.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity(R.layout.activity_main), MainBackGroundRetriever {

    override val background: ViewGroup get() = mainContainer

    private val transformer: MainPageTransformer by inject()
    private val adapter: MainAdapter by inject { parametersOf(this) }
    private val viewPagerChangeCallback: MainViewPagerChangeCallback by inject {
        parametersOf(motionHeader)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        applyFullscreenAndInsets()

        with(mainViewPager) {
            offscreenPageLimit = 1
            adapter = this@MainActivity.adapter
            setPageTransformer(transformer)
            registerOnPageChangeCallback(viewPagerChangeCallback)
            fixOverScrollMode()
        }

        playablesTitleTextView.setOnClickListener {
            motionHeader.progress = 0f
            mainViewPager.currentItem = 0
        }

        searchTitleTextView.setOnClickListener {
            motionHeader.progress = 1f
            mainViewPager.currentItem = 1
        }

        logoImageView.setOnClickListener { showInfoBottomSheet() }
    }

    override fun onDestroy() {
        mainViewPager.unregisterOnPageChangeCallback(viewPagerChangeCallback)
        super.onDestroy()
    }

    private fun applyFullscreenAndInsets() {
        mainContainer.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        window.statusBarColor = resources.getColor(R.color.black_opacity_75, null)

        motionHeaderContainer.doOnApplyWindowInsets { view, windowInsets, viewState ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(top = viewState.margins.top + windowInsets.systemWindowInsetTop)
            }
        }

        logoImageView.doOnApplyWindowInsets { view, windowInsets, viewState ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(top = viewState.margins.top + windowInsets.systemWindowInsetTop)
            }
        }
    }

    @Deprecated(message = "This is a bug in ViewPager2, replace with overScrollMode=\"none\" in layout when fixed")
    private fun ViewPager2.fixOverScrollMode() {
        (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }
}
