package at.florianschuster.playables.base

import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.squareup.leakcanary.RefWatcher
import org.koin.android.ext.android.get

abstract class BaseActivity(@LayoutRes layout: Int) : AppCompatActivity(layout) {

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        get<RefWatcher>().watch(this)
    }
}
