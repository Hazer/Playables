package at.florianschuster.playables.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import at.florianschuster.control.Controller
import com.squareup.leakcanary.RefWatcher
import org.koin.core.KoinComponent
import org.koin.core.get

abstract class BaseController<A, M, S> : ViewModel(), Controller<A, M, S>, KoinComponent {

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        cancel()
        get<RefWatcher>().watch(this)
    }
}