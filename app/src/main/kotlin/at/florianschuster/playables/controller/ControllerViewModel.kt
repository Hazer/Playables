package at.florianschuster.playables.controller

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

abstract class ControllerViewModel<Action : Any, Mutation : Any, State : Any>(
    private val controller: Controller<Action, Mutation, State>
) : ViewModel(), Controller<Action, Mutation, State> by controller {

    val liveDataState: LiveData<State> by lazy { state.asLiveData() }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        controller.cancel()
    }
}