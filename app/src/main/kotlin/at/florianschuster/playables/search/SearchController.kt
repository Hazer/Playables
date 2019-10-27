package at.florianschuster.playables.search

import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.dataFlowOf
import at.florianschuster.data.lce.onEachDataFailure
import at.florianschuster.playables.base.BaseController
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

class SearchController(
    val dataRepo: DataRepo
) : BaseController<SearchController.Action, SearchController.Mutation, SearchController.State>() {
    sealed class Action {
        data class Query(val query: String) : Action()
        object LoadNextPage : Action()
        data class AddGame(val game: SearchResult) : Action()
        object ReloadCurrentQuery : Action()
    }

    sealed class Mutation {
        data class SetQuery(val query: String) : Mutation()
        data class SetSearchItems(val items: Data<List<SearchResult>>) : Mutation()
        data class AppendSearchItems(val items: Data<List<SearchResult>>) : Mutation()
    }

    data class State(
        val query: String = "",
        val page: Int = 1,
        val items: List<SearchResult> = emptyList(),
        val pageLoad: Data<List<SearchResult>> = Data.Uninitialized,
        val firstLoadComplete: Boolean = false
    )

    override val initialState: State = State()

    override fun transformAction(action: Flow<Action>): Flow<Action> =
        action.onStart { emit(Action.Query("")) }

    override fun mutate(action: Action): Flow<Mutation> = when (action) {
        is Action.Query -> flow {
            emit(Mutation.SetQuery(action.query))
            val items = search(action.query, 1)
            emitAll(items.map { Mutation.SetSearchItems(it) })
        }
        is Action.LoadNextPage -> when {
            !currentState.pageLoad.complete -> emptyFlow()
            else -> flow {
                val items = search(currentState.query, currentState.page + 1)
                emitAll(items.map { Mutation.AppendSearchItems(it) })
            }
        }
        is Action.AddGame -> emptyFlow() // todo
        is Action.ReloadCurrentQuery -> flow {
            val items = search(currentState.query, 1)
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

    private fun search(query: String, page: Int) = dataFlowOf {
        dataRepo.search(query, page)
    }
}