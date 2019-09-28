package at.florianschuster.playables.search

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseFragment
import at.florianschuster.playables.controller.Data
import at.florianschuster.playables.detail.DetailActivity
import com.tailoredapps.androidutil.ui.extensions.addScrolledPastItemListener
import com.tailoredapps.androidutil.ui.extensions.afterMeasured
import com.tailoredapps.androidutil.ui.extensions.hideKeyboard
import com.tailoredapps.androidutil.ui.extensions.showKeyBoard
import com.tailoredapps.androidutil.ui.extensions.toast
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import timber.log.Timber

class SearchFragment : BaseFragment(layout = R.layout.fragment_search) {
    private val viewModel: SearchControllerViewModel by viewModel()
    private val adapter: SearchAdapter by inject()

    init {
        lifecycleScope.launchWhenStarted {
            searchRecyclerView.adapter = adapter

            adapter.interaction = { DetailActivity.start(requireContext(), it.id) }

            searchRecyclerView.addScrolledPastItemListener {
                searchScrollButton?.isVisible = it
            }

            searchScrollButton.clicks()
                .onEach { searchRecyclerView.smoothScrollToPosition(0) }
                .launchIn(this)

            searchEditText.textChanges()
                .distinctUntilChanged()
                .map { if (it.isEmpty()) INVISIBLE else VISIBLE }
                .onEach { searchClearButton.visibility = it }
                .launchIn(this)

            searchButton.clicks()
                .onEach { searchEditText.showKeyBoard() }
                .launchIn(this)

            searchClearButton.clicks()
                .onEach {
                    searchEditText.setText("")
                    searchEditText.hideKeyboard()
                }
                .launchIn(this)

            searchEditText.textChanges()
                .drop(1)
                .distinctUntilChanged()
                .debounce(500)
                .map { it.toString() }
                .map { SearchController.Action.Search(it) }
                .onEach { viewModel.action.offer(it) }
                .launchIn(this)

            viewModel.state.map { it.searchItems }
                .distinctUntilChanged()
                .onEach {
                    searchProgressBar.isVisible = it.loading
                    when (it) {
                        is Data.Success -> adapter.submitList(it.element)
                        is Data.Failure -> toast("Error: ${it.error}")
                    }
                }
                .launchIn(this)
        }

        //insets
        lifecycleScope.launchWhenCreated {
            view?.let(ViewCompat::requestApplyInsets)

            searchLayout.setOnApplyWindowInsetsListener { view, windowInsets ->
                view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    updateMargins(bottom = bottomMargin + windowInsets.systemWindowInsetBottom)
                }
                windowInsets
            }

            val activityHeader = activity?.findViewById<View>(R.id.motionHeaderContainer)
            searchRecyclerView.setOnApplyWindowInsetsListener { view, windowInsets ->
                if (activityHeader != null) {
                    activityHeader.afterMeasured {
                        searchLayout.afterMeasured {
                            view.updatePadding(
                                top = windowInsets.systemWindowInsetTop + activityHeader.height,
                                bottom = windowInsets.systemWindowInsetBottom + searchLayout.height + searchLayout.marginBottom
                            )
                        }
                    }
                } else {
                    searchLayout.afterMeasured {
                        view.updatePadding(
                            top = windowInsets.systemWindowInsetTop,
                            bottom = windowInsets.systemWindowInsetBottom + searchLayout.height + searchLayout.marginBottom
                        )
                    }
                }

                windowInsets
            }
        }
    }
}