package at.florianschuster.playables.controller.builder

import at.florianschuster.playables.controller.Controller
import at.florianschuster.playables.controller.Control
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import timber.log.Timber

class ControllerBuilder<Action : Any, Mutation : Any, State : Any> internal constructor() {
    private var controllerScopeInitializer: () -> CoroutineScope = { ControllerScope() }
    private lateinit var stateInitializer: (() -> State)
    private var mutator: (Action) -> Flow<Mutation> = { emptyFlow() }
    private var reducer: (State, Mutation) -> State = { s, _ -> s }
    private var actionTransformer: (Flow<Action>) -> Flow<Action> = { it }
    private var mutationTransformer: (Flow<Mutation>) -> Flow<Mutation> = { it }

    fun initialState(stateInitializer: () -> State) {
        this.stateInitializer = stateInitializer
    }

    fun mutate(mutator: (action: Action) -> Flow<Mutation>) {
        this.mutator = mutator
    }

    fun reduce(reducer: (previousState: State, mutation: Mutation) -> State) {
        this.reducer = reducer
    }

    fun transformAction(actionTransformer: (actionFlow: Flow<Action>) -> Flow<Action>) {
        this.actionTransformer = actionTransformer
    }

    fun transformMutation(mutationTransformer: (mutationFlow: Flow<Mutation>) -> Flow<Mutation>) {
        this.mutationTransformer = mutationTransformer
    }

    fun controllerScope(controllerScopeInitializer: () -> CoroutineScope) {
        this.controllerScopeInitializer = controllerScopeInitializer
    }

    internal fun build(): Controller<Action, Mutation, State> {
        require(::stateInitializer.isInitialized) { "initial state is required" }

        return object : Controller<Action, Mutation, State> {
            override val controllerScope: CoroutineScope = controllerScopeInitializer()

            override val initialState: State = stateInitializer()
            override val currentState: State get() = privateState.value

            private val privateAction: Channel<Action> = Channel(capacity = Channel.CONFLATED)
            override val action: SendChannel<Action> get() = privateAction

            private val privateState = ConflatedBroadcastChannel(initialState)
            override val state: Flow<State> get() = privateState.asFlow()

            init {
                val mutation = transformAction(privateAction.consumeAsFlow())
                    .flatMapMerge {
                        mutate(it).catch { error ->
                            Control.handleError(error)
                            emitAll(emptyFlow())
                        }
                    }

                transformMutation(mutation)
                    .scan(initialState) { s, m -> reduce(s, m) }
                    .catch { error ->
                        Control.handleError(error)
                        emitAll(emptyFlow())
                    }
                    .onEach { privateState.offer(it) }
                    .onCompletion { Timber.w("NOW COMPLETED") }
                    .launchIn(controllerScope)
                // todo use share() or maybe stateFlow
            }

            override fun mutate(action: Action): Flow<Mutation> = mutator(action)

            override fun reduce(previousState: State, mutation: Mutation): State =
                reducer(previousState, mutation)

            override fun transformAction(action: Flow<Action>): Flow<Action> =
                actionTransformer(action)

            override fun transformMutation(mutation: Flow<Mutation>): Flow<Mutation> =
                mutationTransformer(mutation)
        }
    }
}
