package at.florianschuster.playables.search

import at.florianschuster.playables.controller.Controller
import at.florianschuster.playables.controller.ControllerViewModel
import at.florianschuster.playables.controller.Data
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.SearchResult
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.lang.IllegalStateException

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
    dataRepo: DataRepo
) : ControllerViewModel<SearchAction, SearchMutation, SearchState>({
    initialState { SearchState() }

    transformAction { it.onStart { emit(SearchAction.Query("")) } }

    mutate { action ->
        //        throw IllegalStateException("AAA")
        when (action) {
            is SearchAction.Query -> {
                flow {
                    //                    throw IllegalStateException("BBB")
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
    }

    reduce { previousState, mutation ->
//        throw IllegalStateException("CCC")
        when (mutation) {
            is SearchMutation.SetSearchItems -> previousState.copy(searchItems = mutation.searchItems)
        }
    }
})