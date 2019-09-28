package at.florianschuster.playables.search

import at.florianschuster.playables.controller.ControllerViewModel
import at.florianschuster.playables.controller.Data
import at.florianschuster.playables.controller.DefaultController
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.lang.Exception

class SearchControllerViewModel(
    dataRepo: DataRepo
) : ControllerViewModel<SearchController.Action, SearchController.Mutation, SearchController.State>(
    SearchController(dataRepo)
)

class SearchController(
    private val dataRepo: DataRepo
) : DefaultController<SearchController.Action, SearchController.Mutation, SearchController.State>(
    initialState = State()
) {

    sealed class Action {
        data class Search(val query: String) : Action()
    }

    sealed class Mutation {
        data class SetSearchItems(val searchItems: Data<List<SearchResult>>) : Mutation()
    }

    data class State(
        val searchItems: Data<List<SearchResult>> = Data.Uninitialized
    )

    override fun transformAction(action: Flow<Action>): Flow<Action> {
        return action.onStart { emit(Action.Search("")) }
    }

    override fun mutate(action: Action): Flow<Mutation> = when (action) {
        is Action.Search -> {
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
            }.map { Mutation.SetSearchItems(it) }
        }
    }

    override fun reduce(previousState: State, mutation: Mutation): State = when (mutation) {
        is Mutation.SetSearchItems -> previousState.copy(searchItems = mutation.searchItems)
    }
}