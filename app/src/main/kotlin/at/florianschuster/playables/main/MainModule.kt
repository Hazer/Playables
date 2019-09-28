package at.florianschuster.playables.main

import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import org.koin.dsl.module

internal val mainModule = module {
    factory { MainPageTransformer() }
    factory { (activity: AppCompatActivity) -> MainAdapter(activity) }
    factory { (motionLayout: MotionLayout) -> MainViewPagerChangeCallback(motionLayout) }
}