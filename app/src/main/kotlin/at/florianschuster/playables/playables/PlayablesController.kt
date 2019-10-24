package at.florianschuster.playables.playables

import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.mapAsData
import at.florianschuster.playables.base.BaseController
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PlayablesController(
    private val dataRepo: DataRepo
) : BaseController<PlayablesController.Action, PlayablesController.Mutation, PlayablesController.State>() {

    sealed class Action

    sealed class Mutation {
        data class SetGames(val games: Data<List<Game>>) : Mutation()
    }

    data class State(
        val games: Data<List<Game>> = Data.Uninitialized
    )

    override val initialState: State = State()

    override fun transformMutation(mutation: Flow<Mutation>): Flow<Mutation> {
        val flow2: Flow<Mutation> = dataRepo.playables().mapAsData().map { Mutation.SetGames(it) }
        return flowOf(mutation, flow2).flattenMerge()
    }

    override fun reduce(previousState: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetGames -> previousState.copy(games = mutation.games)
    }
}