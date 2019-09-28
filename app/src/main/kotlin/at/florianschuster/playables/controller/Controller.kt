package at.florianschuster.playables.controller

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface Controller<Action : Any, Mutation : Any, State : Any> {
    val initialState: State
    var currentState: State

    val action: SendChannel<Action>
    val state: Flow<State>

    fun mutate(action: Action): Flow<Mutation> = emptyFlow()
    fun reduce(previousState: State, mutation: Mutation): State = previousState

    fun transformAction(action: Flow<Action>): Flow<Action> = action
    fun transformMutation(mutation: Flow<Mutation>): Flow<Mutation> = mutation

    fun cancel()
}