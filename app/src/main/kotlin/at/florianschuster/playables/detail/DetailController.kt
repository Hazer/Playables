package at.florianschuster.playables.detail

import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.dataFlowOf
import at.florianschuster.playables.base.BaseController
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class DetailController(
    private val gameId: Long,
    private val dataRepo: DataRepo
) : BaseController<DetailController.Action, DetailController.Mutation, DetailController.State>() {
    sealed class Action {
        object LoadGame : Action()
    }

    sealed class Mutation {
        data class SetGame(val game: Data<Game>) : Mutation()
    }

    data class State(
        val game: Data<Game> = Data.Uninitialized
    )

    override val initialState: State = State()

    override fun transformAction(action: Flow<Action>): Flow<Action> =
        action.onStart { emit(Action.LoadGame) }

    override fun mutate(action: Action): Flow<Mutation> = when (action) {
        is Action.LoadGame -> {
            dataFlowOf { dataRepo.get(gameId) }.map { Mutation.SetGame(it) }
        }
    }

    override fun reduce(previousState: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetGame -> previousState.copy(game = mutation.game)
    }
}