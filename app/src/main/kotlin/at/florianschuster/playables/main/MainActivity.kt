/*
 * Copyright 2019 Florian Schuster.
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

package at.florianschuster.playables.main

import android.os.Bundle
import android.view.View.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseActivity
import at.florianschuster.playables.base.ui.BaseViewModel
import at.florianschuster.playables.base.ui.launchWith
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.SearchResult
import com.tailoredapps.androidutil.async.Async
import com.tailoredapps.androidutil.ui.extensions.addScrolledPastItemListener
import com.tailoredapps.androidutil.ui.extensions.afterMeasured
import com.tailoredapps.androidutil.ui.extensions.hideKeyboard
import com.tailoredapps.androidutil.ui.extensions.showKeyBoard
import com.tailoredapps.androidutil.ui.extensions.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import timber.log.Timber
import java.lang.Exception

class MainActivity : BaseActivity(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModel()
    private val adapter: MainAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        applyFullscreen()

        mainRecyclerView.adapter = adapter

        mainRecyclerView.addScrolledPastItemListener { scrollFloatingActionButton?.isVisible = it }

        scrollFloatingActionButton.clicks()
            .onEach { mainRecyclerView.smoothScrollToPosition(0) }
            .launchWith(lifecycle)

        searchEditText.textChanges()
            .distinctUntilChanged()
            .map { if (it.isNotEmpty()) VISIBLE else INVISIBLE }
            .onEach { searchClearButton.visibility = it }
            .launchWith(lifecycle)

        searchButton.clicks()
            .onEach { searchEditText.showKeyBoard() }
            .launchWith(lifecycle)

        searchClearButton.clicks()
            .onEach {
                searchEditText.setText("")
                searchEditText.hideKeyboard()
            }
            .launchWith(lifecycle)

        searchEditText.textChanges()
            .drop(1)
            .distinctUntilChanged()
            .debounce(500)
            .onEach { viewModel.search(it.toString()) }
            .launchWith(lifecycle)

        viewModel.searchItems.observe(this, Observer {
            searchProgressBar.isVisible = it.loading
            when (it) {
                is Async.Success -> adapter.submitList(it.element)
                is Async.Error -> toast("Error: ${it.error}")
            }
        })
    }

    private fun applyFullscreen() {
        mainContainer.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        mainRecyclerView.setOnApplyWindowInsetsListener { view, insets ->
            searchLayout.afterMeasured {
                view.updatePadding(bottom = insets.systemWindowInsetBottom + height)
            }
            insets
        }

        titleTextView.setOnApplyWindowInsetsListener { view, insets ->
            view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(top = insets.systemWindowInsetTop)
            }
            view.afterMeasured {
                mainRecyclerView.updatePadding(top = insets.systemWindowInsetTop + height)
            }
            insets
        }

        searchLayout.setOnApplyWindowInsetsListener { view, insets ->
            view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = insets.systemWindowInsetBottom)
            }
            insets
        }
    }
}

class MainViewModel(
    private val dataRepo: DataRepo
) : BaseViewModel() {

    val searchItems: LiveData<Async<List<SearchResult>>>
        get() = _searchItems

    private val _searchItems: MutableLiveData<Async<List<SearchResult>>> =
        MutableLiveData(Async.Uninitialized)

    init {
        search("")
    }

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