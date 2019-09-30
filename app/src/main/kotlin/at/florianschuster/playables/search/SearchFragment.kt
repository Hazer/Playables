package at.florianschuster.playables.search

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseFragment
import at.florianschuster.playables.base.ui.doOnApplyWindowInsets
import at.florianschuster.playables.controller.Data
import at.florianschuster.playables.controller.bind
import at.florianschuster.playables.detail.startDetail
import at.florianschuster.playables.main.retrieveActivityBlurredScreenShot
import com.tailoredapps.androidutil.ui.extensions.addScrolledPastItemListener
import com.tailoredapps.androidutil.ui.extensions.afterMeasured
import com.tailoredapps.androidutil.ui.extensions.hideKeyboard
import com.tailoredapps.androidutil.ui.extensions.showKeyBoard
import com.tailoredapps.androidutil.ui.extensions.toast
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

            adapter.interaction = {
                lifecycleScope.launch {
                    val screenShotFile = retrieveActivityBlurredScreenShot()
                    requireContext().startDetail(it.id, screenShotFile)
                }
            }

            searchRecyclerView.addScrolledPastItemListener {
                searchScrollButton?.visibility = if (it) VISIBLE else INVISIBLE
            }

            searchScrollButton.clicks()
                .bind { searchRecyclerView.smoothScrollToPosition(0) }
                .launchIn(this)

            searchEditText.textChanges()
                .bind { searchClearButton.isVisible = it.isNotEmpty() }
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

            searchEditText.textChanges()
                .drop(1)
                .debounce(500)
                .map { SearchController.Action.Search(it.toString()) }
                .bind { viewModel.action.offer(it) }
                .launchIn(this)

            viewModel.state.map { it.searchItems }
                .distinctUntilChanged()
                .bind {
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

            searchLayout.doOnApplyWindowInsets { view, windowInsets, _, initialMargin ->
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(bottom = initialMargin.bottom + windowInsets.systemWindowInsetBottom)
                }
            }

            val activityHeader = activity?.findViewById<View>(R.id.motionHeaderContainer)
            searchRecyclerView.doOnApplyWindowInsets { view, windowInsets, initialPadding, _ ->
                if (activityHeader != null) {
                    activityHeader.afterMeasured {
                        searchLayout.afterMeasured {
                            view.updatePadding(
                                top = windowInsets.systemWindowInsetTop + activityHeader.height + initialPadding.top,
                                bottom = windowInsets.systemWindowInsetBottom + searchLayout.height + searchLayout.marginBottom + initialPadding.bottom
                            )
                        }
                    }
                } else {
                    searchLayout.afterMeasured {
                        view.updatePadding(
                            top = windowInsets.systemWindowInsetTop + initialPadding.top,
                            bottom = windowInsets.systemWindowInsetBottom + searchLayout.height + searchLayout.marginBottom + initialPadding.bottom
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        searchEditText.hideKeyboard()
        super.onPause()
    }
}