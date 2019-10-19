package at.florianschuster.playables.search

import at.florianschuster.data.lce.Data
import at.florianschuster.playables.base.ui.BaseController
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeout
import timber.log.Timber

sealed class SearchAction {
    data class Query(val query: String) : SearchAction()
}

sealed class SearchMutation {
    data class SetSearchItems(val searchItems: Data<List<SearchResult>>) : SearchMutation()
}

data class SearchState(
    val searchItems: Data<List<SearchResult>> = Data.Uninitialized
)

class SearchControllerViewModel(
    private val dataRepo: DataRepo
) : BaseController<SearchAction, SearchMutation, SearchState>() {

    override val initialState: SearchState = SearchState()

    override fun transformAction(action: Flow<SearchAction>): Flow<SearchAction> {
        return action.onStart { emit(SearchAction.Query("")) }
    }

    override fun mutate(action: SearchAction): Flow<SearchMutation> = when (action) {
        is SearchAction.Query -> {
            flow {
                emit(Data.Loading)
                val data: Data<List<SearchResult>> = try {
                    withTimeout(3000) {
                        Data.Success(dataRepo.search(action.query, 1))
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                    Data.Failure(e)
                }
                emit(data)
            }.map { SearchMutation.SetSearchItems(it) }
        }
    }

    override fun reduce(previousState: SearchState, mutation: SearchMutation): SearchState =
        when (mutation) {
            is SearchMutation.SetSearchItems -> previousState.copy(searchItems = mutation.searchItems)
        }
}