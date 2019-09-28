package at.florianschuster.playables.controller

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

//data class ControllerBuilder<Action : Any, Mutation : Any, State : Any>(
//    var mutator: (action: Action) -> Flow<Mutation> = { emptyFlow() },
//    var reducer: (previousState: State, mutation: Mutation) -> State = { previousState, _ -> previousState },
//    var actionTransformer: (action: Flow<Action>) -> Flow<Action> = { it },
//    var mutationTransformer: (mutation: Flow<Mutation>) -> Flow<Mutation> = { it }
//)
//
//fun <Action : Any, Mutation : Any, State : Any> controller(
//    initialState: State,
//    builderAdapter: ControllerBuilder<Action, Mutation, State>.() -> Unit
//): Controller<Action, Mutation, State> {
//    val builder = ControllerBuilder<Action, Mutation, State>()
//    builder.builderAdapter()
//
//    return object : Controller<Action, Mutation, State> {
//        private val scope = object : CoroutineScope {
//            override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
//        }
//
//        override val initialState: State = initialState
//        override var currentState: State = initialState
//
//        private val _action: Channel<Action> = Channel(capacity = Channel.CONFLATED)
//        override val action: SendChannel<Action> get() = _action
//
//        private val _state: BroadcastChannel<State> = BroadcastChannel(capacity = Channel.CONFLATED)
//        override val state: Flow<State> get() = _state.asFlow()
//
//        init {
//            val mutation = transformAction(_action.consumeAsFlow())
//                .flatMapMerge {
//                    mutate(it)
//                        .catch { error ->
//                            Timber.e(error)
//                            emitAll(emptyFlow())
//                        }
//                }
//
//            transformMutation(mutation)
//                .scan(initialState) { a, b -> reduce(a, b) }
//                .catch { error ->
//                    Timber.e(error)
//                    emitAll(emptyFlow())
//                }
//                .onEach {
//                    currentState = it
//                    _state.offer(it)
//                }
//                .launchIn(scope)
//        }
//
//        override fun mutate(action: Action): Flow<Mutation> = builder.mutator(action)
//
//        override fun reduce(previousState: State, mutation: Mutation): State =
//            reduce(previousState, mutation)
//
//        override fun transformAction(action: Flow<Action>): Flow<Action> =
//            builder.actionTransformer(action)
//
//        override fun transformMutation(mutation: Flow<Mutation>): Flow<Mutation> =
//            builder.mutationTransformer(mutation)
//
//        override fun cancel() {
//            scope.cancel()
//        }
//    }
//}

@FlowPreview
@ExperimentalCoroutinesApi
abstract class DefaultController<Action : Any, Mutation : Any, State : Any>(
    override val initialState: State
) : Controller<Action, Mutation, State> {

    private val scope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
    }

    override var currentState: State = initialState

    private val _action: Channel<Action> = Channel(capacity = Channel.CONFLATED)
    override val action: SendChannel<Action> get() = _action

    private val _state: BroadcastChannel<State> = BroadcastChannel(capacity = Channel.CONFLATED)
    override val state: Flow<State> get() = _state.asFlow()

    init {
        val mutation = transformAction(_action.consumeAsFlow())
            .flatMapMerge {
                mutate(it)
                    .catch { error ->
                        Timber.e(error)
                        emitAll(emptyFlow())
                    }
            }

        transformMutation(mutation)
            .scan(initialState) { a, b -> reduce(a, b) }
            .catch { error ->
                Timber.e(error)
                emitAll(emptyFlow())
            }
            .onEach {
                currentState = it
                _state.offer(it)
            }
            .launchIn(scope)
    }

    @CallSuper
    override fun cancel() {
        scope.cancel()
    }
}

//fun <Action : Any, Mutation : Any, State : Any> controller(
//    initialState: State
//): Controller<Action, Mutation, State> = DefaultController(initialState)
