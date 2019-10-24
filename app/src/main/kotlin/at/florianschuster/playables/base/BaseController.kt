package at.florianschuster.playables.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import at.florianschuster.control.Controller
import com.squareup.leakcanary.RefWatcher
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseController<A, M, S> : ViewModel(), Controller<A, M, S>, KoinComponent {
    private val refWatcher: RefWatcher by inject()

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        cancel()
        refWatcher.watch(this)
    }
}