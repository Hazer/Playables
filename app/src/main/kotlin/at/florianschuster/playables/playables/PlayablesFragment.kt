package at.florianschuster.playables.playables

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import at.florianschuster.control.bind
import at.florianschuster.control.changesFrom
import at.florianschuster.data.lce.Data
import at.florianschuster.playables.R
import at.florianschuster.playables.base.BaseFragment
import at.florianschuster.playables.detail.openDetailScreen
import at.florianschuster.playables.main.retrieveActivityBlurredScreenShot
import at.florianschuster.playables.util.doOnApplyWindowInsets
import com.tailoredapps.androidutil.ui.extensions.addScrolledPastItemListener
import com.tailoredapps.androidutil.ui.extensions.afterMeasured
import com.tailoredapps.androidutil.ui.extensions.toast
import kotlinx.android.synthetic.main.fragment_playables.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.ldralighieri.corbind.view.clicks

class PlayablesFragment : BaseFragment(layout = R.layout.fragment_playables) {
    private val controller: PlayablesController by viewModel()
    private val adapter: PlayablesAdapter by inject()

    init {
        lifecycleScope.launchWhenCreated {
            applyInsets()
            playablesRecyclerView.adapter = adapter

            adapter.interaction = {
                when (it) {
                    is PlayablesAdapterInteraction.Clicked -> {
                        lifecycleScope.launch {
                            val screenShotFile = retrieveActivityBlurredScreenShot()
                            requireContext().openDetailScreen(it.game.id, screenShotFile)
                        }
                    }
                    is PlayablesAdapterInteraction.PlayedClicked -> {
                        // todo
                    }
                }
            }

            playablesRecyclerView.addScrolledPastItemListener {
                playablesScrollButton?.isVisible = it
            }

            playablesScrollButton.clicks()
                .bind { playablesRecyclerView.smoothScrollToPosition(0) }
                .launchIn(this)

            controller.state.changesFrom { it.games }
                .bind { games ->
                    playablesRecyclerView.isVisible = !games().isNullOrEmpty()
                    layoutPlayablesEmpty.isVisible = games().isNullOrEmpty()

                    when (games) {
                        is Data.Success -> adapter.submitList(games.value)
                        is Data.Failure -> toast("Error todo")
                    }
                }
                .launchIn(this)
        }
    }

    private fun applyInsets() {
        view?.let(ViewCompat::requestApplyInsets)

        playablesScrollButton.doOnApplyWindowInsets { view, windowInsets, _, initialMargin ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(bottom = initialMargin.bottom + windowInsets.systemWindowInsetBottom)
            }
        }

        val activityHeader = activity?.findViewById<View>(R.id.motionHeaderContainer)
        playablesRecyclerView.doOnApplyWindowInsets { view, windowInsets, initialPadding, _ ->
            if (activityHeader != null) {
                activityHeader.afterMeasured {
                    view.updatePadding(
                        top = windowInsets.systemWindowInsetTop + activityHeader.height + initialPadding.top,
                        bottom = windowInsets.systemWindowInsetBottom + initialPadding.bottom
                    )
                }
            } else {
                view.updatePadding(
                    top = windowInsets.systemWindowInsetTop + initialPadding.top,
                    bottom = windowInsets.systemWindowInsetBottom + initialPadding.bottom
                )
            }
        }
    }
}