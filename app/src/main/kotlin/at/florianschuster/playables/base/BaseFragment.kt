package at.florianschuster.playables.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.squareup.leakcanary.RefWatcher
import org.koin.android.ext.android.get

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    override fun onDestroy() {
        super.onDestroy()
        get<RefWatcher>().watch(this)
    }
}