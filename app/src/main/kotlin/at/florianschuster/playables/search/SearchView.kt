package at.florianschuster.playables.search

import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import at.florianschuster.control.bind
import at.florianschuster.control.changesFrom
import at.florianschuster.data.lce.Data
import at.florianschuster.playables.R
import at.florianschuster.playables.base.BaseFragment
import at.florianschuster.playables.detail.DetailView
import at.florianschuster.playables.main.MainHeaderChild
import at.florianschuster.playables.main.retrieveActivityBlurredScreenShot
import at.florianschuster.playables.util.scrolledPastItemChanges
import com.tailoredapps.androidutil.ui.extensions.afterMeasured
import com.tailoredapps.androidutil.ui.extensions.hideKeyboard
import com.tailoredapps.androidutil.ui.extensions.shouldLoadMore
import com.tailoredapps.androidutil.ui.extensions.showKeyBoard
import com.tailoredapps.androidutil.ui.extensions.toast
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_error.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.ldralighieri.corbind.recyclerview.scrollEvents
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges

class SearchView : BaseFragment(layout = R.layout.fragment_search), MainHeaderChild {
    private val controller: SearchController by viewModel()
    private val adapter: SearchAdapter by inject()

    override val parent: FragmentActivity get() = requireActivity()

    init {
        lifecycleScope.launchWhenCreated {
            applyInsets()
            searchRecyclerView.adapter = adapter
        }

        lifecycleScope.launchWhenStarted {
            searchRecyclerView.scrolledPastItemChanges(itemIndex = 0)
                .map { if (it) VISIBLE else GONE }
                .bind(to = searchScrollButton::setVisibility)
                .launchIn(this)

            searchScrollButton.clicks()
                .bind { searchRecyclerView.smoothScrollToPosition(0) }
                .launchIn(this)

            searchEditText.textChanges()
                .map { if (it.isNotEmpty()) VISIBLE else INVISIBLE }
                .bind(to = searchClearButton::setVisibility)
                .launchIn(this)

            searchButton.clicks()
                .bind { searchEditText.showKeyBoard() }
                .launchIn(this)

            searchClearButton.clicks()
                .bind {
                    searchEditText.setText("")
                    searchEditText.hideKeyboard()
                }
                .launchIn(this)

            adapter.interaction
                .filterIsInstance<SearchAdapterInteraction.Click>()
                .map { it.gameId to retrieveActivityBlurredScreenShot() }
                .bind { (id, screenshot) ->
                    DetailView.start(requireContext(), id, screenshot)
                }
                .launchIn(this)

            adapter.interaction
                .filterIsInstance<SearchAdapterInteraction.AddGame>()
                .map { SearchController.Action.AddGame(it.game) }
                .bind(to = controller.action)
                .launchIn(this)

            searchEditText.textChanges()
                .drop(1)
                .debounce(500)
                .map { SearchController.Action.Query(it.toString()) }
                .bind(to = controller.action)
                .launchIn(this)

            searchRecyclerView.scrollEvents()
                .sample(500)
                .filter { it.view.shouldLoadMore() }
                .map { SearchController.Action.LoadNextPage }
                .bind(to = controller.action)
                .launchIn(this)

            layoutSearchError.errorButton.clicks()
                .map { SearchController.Action.ReloadCurrentQuery }
                .bind(to = controller.action)
                .launchIn(this)

            controller.state.changesFrom { it.items }
                .bind(to = adapter::submitList)
                .launchIn(this)

            controller.state.changesFrom { it.pageLoad to it.firstLoadComplete }
                .bind { (results, firstLoadComplete) ->
                    searchProgressBar.isVisible = results.loading
                    layoutSearchLoading.isVisible = results.loading && !firstLoadComplete
                    searchRecyclerView.isVisible =
                        results.successful || (!results.loading && firstLoadComplete)
                    layoutSearchError.isVisible = results.failed
                }
                .launchIn(this)
        }
    }

    private fun applyInsets() {
        searchLayout.doOnApplyWindowInsets { view, windowInsets, viewState ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(bottom = viewState.margins.bottom + windowInsets.systemWindowInsetBottom)
            }
        }

        afterMainHeaderMeasure { header ->
            listOf(
                layoutSearchLoading,
                searchRecyclerView
            ).forEach {
                it.doOnApplyWindowInsets { view, windowInsets, viewState ->
                    searchLayout.afterMeasured {
                        view.updatePadding(
                            top = windowInsets.systemWindowInsetTop + header.height + viewState.paddings.top,
                            bottom = windowInsets.systemWindowInsetBottom + height + marginBottom + viewState.paddings.bottom
                        )
                    }
                }
            }
            layoutSearchError.doOnApplyWindowInsets { view, windowInsets, viewState ->
                view.updatePadding(
                    top = windowInsets.systemWindowInsetTop + header.height + viewState.paddings.top,
                    bottom = windowInsets.systemWindowInsetBottom + viewState.paddings.bottom
                )
            }
        }
    }

    override fun onPause() {
        searchEditText.hideKeyboard()
        super.onPause()
    }
}