/*
 * Copyright 2019 Florian Schuster (https://florianschuster.at/).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.florianschuster.playables.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.*
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseFragment
import at.florianschuster.playables.base.ui.BaseViewModel
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.SearchResult
import com.tailoredapps.androidutil.async.Async
import com.tailoredapps.androidutil.ui.extensions.toast
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.ldralighieri.corbind.widget.textChanges
import timber.log.Timber
import java.lang.Exception

class SearchFragment : BaseFragment(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModel()
    private val adapter: SearchAdapter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchRecyclerView.adapter = adapter

        adapter.interaction = {
            navController.navigate(SearchFragmentDirections.actionSearchToDetail(it))
        }

        searchEditText.textChanges()
            .distinctUntilChanged()
            .debounce(300)
            .map { it.toString() }
            .filter { it.isNotEmpty() }
            .onEach { viewModel.search(it) }
            .launchIn(lifecycle.coroutineScope)

        viewModel.searchItems.observe(this, Observer {
            progressBar.isVisible = it.loading
            when (it) {
                is Async.Success -> adapter.submitList(it.element)
                is Async.Error -> toast("Error: ${it.error}")
            }
        })
    }
}

class SearchViewModel(
    private val dataRepo: DataRepo
) : BaseViewModel() {
    val searchItems: LiveData<Async<List<SearchResult>>>
        get() = _searchItems

    private val _searchItems: MutableLiveData<Async<List<SearchResult>>> =
        MutableLiveData(Async.Uninitialized)

    private var searchJob: Job? = null
    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchItems.postValue(Async.Loading)
            try {
                val data = dataRepo.search(query, 1)
                _searchItems.value = Async.success(data)
            } catch (exception: Exception) {
                Timber.e(exception)
                _searchItems.value = Async.error(exception)
            }
        }
    }
}