package at.florianschuster.playables.detail

import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.mapAsData
import at.florianschuster.playables.base.BaseController
import at.florianschuster.playables.core.GamesRepo
import at.florianschuster.playables.core.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class DetailController(
    private val gameId: Long,
    private val gamesRepo: GamesRepo
) : BaseController<DetailController.Action, DetailController.Mutation, DetailController.State>() {
    sealed class Action {
        object AddGame : Action()
        object RemoveGame : Action()
        object SetGamePlayed : Action()
        object SetGameNotPlayed : Action()
    }

    sealed class Mutation {
        data class SetGame(val game: Data<Game>) : Mutation()
    }

    data class State(
        val game: Data<Game> = Data.Uninitialized
    )

    override val initialState: State = State()

    override fun transformMutation(mutation: Flow<Mutation>): Flow<Mutation> {
        val gameFlow = flow<Data<Game>> {
            emit(Data.Loading)
            emitAll(gamesRepo.observe(gameId).mapAsData())
        }.map { Mutation.SetGame(it) }
        return flowOf(mutation, gameFlow).flattenMerge()
    }

    override fun mutate(action: Action): Flow<Mutation> = when (action) {
        is Action.AddGame -> flow {
            val game = currentState.game() ?: return@flow
            gamesRepo.add(game)
        }
        is Action.RemoveGame -> flow {
            val game = currentState.game() ?: return@flow
            gamesRepo.remove(game.id)
        }
        is Action.SetGamePlayed -> flow {
            val game = currentState.game() ?: return@flow
            if (!game.added) gamesRepo.add(game)
            gamesRepo.setPlayed(game.id, true)
        }
        is Action.SetGameNotPlayed -> flow {
            val game = currentState.game() ?: return@flow
            if (!game.added) gamesRepo.add(game)
            gamesRepo.setPlayed(game.id, false)
        }
    }

    override fun reduce(previousState: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetGame -> previousState.copy(game = mutation.game)
    }
}