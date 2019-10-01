package at.florianschuster.playables.controller

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.cancel

abstract class ControllerViewModel<Action : Any, Mutation : Any, State : Any>(
    initializer: ControllerBuilder<Action, Mutation, State>.() -> Unit
) : ViewModel(), Controller<Action, Mutation, State> by Controller(initializer) {
    override fun onCleared() {
        super.onCleared()
        controllerScope.cancel()
    }
}