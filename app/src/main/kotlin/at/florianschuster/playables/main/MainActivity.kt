package at.florianschuster.playables.main

import android.os.Bundle
import android.view.View.*
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.forEach
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_header.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity(R.layout.activity_main) {

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
        }

        playablesTitleTextView.setOnClickListener {
            motionHeader.progress = 0f
            mainViewPager.currentItem = 0
        }
        searchTitleTextView.setOnClickListener {
            motionHeader.progress = 1f
            mainViewPager.currentItem = 1
        }
    }

    override fun onDestroy() {
        mainViewPager.unregisterOnPageChangeCallback(viewPagerChangeCallback)
        super.onDestroy()
    }

    private fun applyFullscreenAndInsets() {
        mainContainer.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        motionHeaderContainer.setOnApplyWindowInsetsListener { view, insets ->
            view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(top = insets.systemWindowInsetTop)
            }
            insets
        }

        mainViewPager.setOnApplyWindowInsetsListener { view, windowInsets ->
            var consumed = false
            (view as? ViewGroup)?.forEach { child ->
                val childResult = child.dispatchApplyWindowInsets(windowInsets)
                if (childResult.isConsumed) consumed = true
            }
            if (consumed) windowInsets.consumeSystemWindowInsets() else windowInsets
        }
    }
}
