package at.florianschuster.playables.search

import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.dataOf
import at.florianschuster.playables.base.BaseController
import at.florianschuster.playables.core.GamesRepo
import at.florianschuster.playables.core.model.Game
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class SearchController(
    val gamesRepo: GamesRepo
) : BaseController<SearchController.Action, SearchController.Mutation, SearchController.State>() {
    sealed class Action {
        data class Query(val query: String) : Action()
        object LoadNextPage : Action()
        data class AddGame(val game: Game) : Action()
        object ReloadCurrentQuery : Action()
    }

    sealed class Mutation {
        data class SetQuery(val query: String) : Mutation()
        data class SetSearchItems(val items: Data<List<Game>>) : Mutation()
        data class AppendSearchItems(val items: Data<List<Game>>) : Mutation()
    }

    data class State(
        val query: String = "",
        val page: Int = 1,
        val items: List<Game> = emptyList(),
        val pageLoad: Data<List<Game>> = Data.Uninitialized,
        val firstLoadComplete: Boolean = false
    )

    override val initialState: State = State()

    override fun transformAction(action: Flow<Action>): Flow<Action> =
        action.onStart { emit(Action.Query("")) }

    override fun mutate(action: Action): Flow<Mutation> = when (action) {
        is Action.Query -> flow {
            emit(Mutation.SetQuery(action.query))
            val items = searchFlow(action.query, 1)
            emitAll(items.map { Mutation.SetSearchItems(it) })
        }
        is Action.LoadNextPage -> when {
            !currentState.pageLoad.complete -> emptyFlow()
            else -> flow {
                val items = searchFlow(currentState.query, currentState.page + 1)
                emitAll(items.map { Mutation.AppendSearchItems(it) })
            }
        }
        is Action.AddGame -> flow { gamesRepo.add(action.game) }
        is Action.ReloadCurrentQuery -> flow {
            val items = searchFlow(currentState.query, 1)
            emitAll(items.map { Mutation.SetSearchItems(it) })
        }
    }

    override fun reduce(previousState: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetQuery -> previousState.copy(query = mutation.query)
        is Mutation.SetSearchItems -> {
            when (mutation.items) {
                is Data.Success -> previousState.copy(
                    page = 1,
                    pageLoad = mutation.items,
                    items = mutation.items.value,
                    firstLoadComplete = true
                )
                else -> previousState.copy(pageLoad = mutation.items)
            }
        }
        is Mutation.AppendSearchItems -> {
            when (mutation.items) {
                is Data.Success -> previousState.copy(
                    page = previousState.page + 1,
                    pageLoad = mutation.items,
                    items = previousState.items + mutation.items.value
                )
                else -> previousState.copy(pageLoad = mutation.items)
            }
        }
    }

    private fun searchFlow(query: String, page: Int) = flow<Data<List<Game>>> {
        emit(Data.Loading)
        delay(300) // ui sugar
        dataOf { gamesRepo.search(query, page) }
    }
}