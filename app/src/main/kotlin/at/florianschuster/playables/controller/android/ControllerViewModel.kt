package at.florianschuster.playables.controller.android

import androidx.lifecycle.ViewModel
import at.florianschuster.playables.controller.Controller
import at.florianschuster.playables.controller.builder.ControllerBuilder
import kotlinx.coroutines.cancel

abstract class ControllerViewModel<Action : Any, Mutation : Any, State : Any>(
    initializer: ControllerBuilder<Action, Mutation, State>.() -> Unit
) : ViewModel(), Controller<Action, Mutation, State> by Controller(
    initializer
) {
    override fun onCleared() {
        super.onCleared()
        controllerScope.cancel()
    }
}