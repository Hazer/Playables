package at.florianschuster.playables.search

import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.dataFlowOf
import at.florianschuster.playables.base.BaseController
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeout

class SearchController(
    private val dataRepo: DataRepo
) : BaseController<SearchController.Action, SearchController.Mutation, SearchController.State>() {
    sealed class Action {
        data class Query(val query: String) : Action()
    }

    sealed class Mutation {
        data class SetSearchItems(val searchItems: Data<List<SearchResult>>) : Mutation()
    }

    data class State(
        val searchItems: Data<List<SearchResult>> = Data.Uninitialized
    )

    override val initialState: State = State()

    override fun transformAction(action: Flow<Action>): Flow<Action> =
        action.onStart { emit(Action.Query("")) }

    override fun mutate(action: Action): Flow<Mutation> = when (action) {
        is Action.Query -> {
            dataFlowOf { withTimeout(3000) { dataRepo.search(action.query, 1) } }
                .map { Mutation.SetSearchItems(it) }
        }
    }

    override fun reduce(previousState: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetSearchItems -> previousState.copy(searchItems = mutation.searchItems)
    }
}