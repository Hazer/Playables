package at.florianschuster.playables.controller

import at.florianschuster.playables.controller.build.ControllerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface Controller<Action : Any, Mutation : Any, State : Any> {
    val controllerScope: CoroutineScope

    val initialState: State
    val currentState: State

    val action: SendChannel<Action>
    val state: Flow<State>

    fun mutate(action: Action): Flow<Mutation> = emptyFlow()
    fun reduce(previousState: State, mutation: Mutation): State = previousState

    fun transformAction(action: Flow<Action>): Flow<Action> = action
    fun transformMutation(mutation: Flow<Mutation>): Flow<Mutation> = mutation

    companion object {
        operator fun <Action : Any, Mutation : Any, State : Any> invoke(
            initializer: ControllerBuilder<Action, Mutation, State>.() -> Unit
        ): Controller<Action, Mutation, State> {
            val builder = ControllerBuilder<Action, Mutation, State>()
            initializer(builder)
            return builder.build()
        }
    }
}
