package at.florianschuster.playables.playables

import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.mapAsData
import at.florianschuster.playables.base.BaseController
import at.florianschuster.playables.core.GamesRepo
import at.florianschuster.playables.core.model.Game
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PlayablesController(
    private val gamesRepo: GamesRepo
) : BaseController<PlayablesController.Action, PlayablesController.Mutation, PlayablesController.State>() {

    sealed class Action {
        object ReloadGames : Action()
        data class SetGamePlayed(val gameId: Long, val played: Boolean) : Action()
    }

    sealed class Mutation {
        data class SetGames(val games: Data<List<Game>>) : Mutation()
    }

    data class State(
        val games: Data<List<Game>> = Data.Uninitialized
    )

    override val initialState: State = State()

    override fun transformMutation(mutation: Flow<Mutation>): Flow<Mutation> {
        val playablesUpdateFlow = flow<Mutation> {
            emit(Mutation.SetGames(Data.Loading))
            delay(300)
            emitAll(gamesRepo.observeMyGames().mapAsData().map { Mutation.SetGames(it) })
        }
        return flowOf(mutation, playablesUpdateFlow).flattenMerge()
    }

    override fun mutate(action: Action): Flow<Mutation> = when (action) {
        is Action.ReloadGames -> flow {
            emit(Mutation.SetGames(Data.Loading))
            delay(300)
            gamesRepo.reloadMyGames()
        }
        is Action.SetGamePlayed -> flow { gamesRepo.setPlayed(action.gameId, action.played) }
    }

    override fun reduce(previousState: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetGames -> previousState.copy(games = mutation.games)
    }
}