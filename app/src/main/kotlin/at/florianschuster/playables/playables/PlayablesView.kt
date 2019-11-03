package at.florianschuster.playables.playables

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import at.florianschuster.playables.playables.adapter.PlayablesAdapter
import at.florianschuster.playables.playables.adapter.PlayablesAdapterInteraction
import at.florianschuster.playables.util.scrolledPastItemChanges
import com.tailoredapps.androidutil.ui.extensions.smoothScrollUp
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_playables.*
import kotlinx.android.synthetic.main.fragment_error.view.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks

class PlayablesView : BaseFragment(layout = R.layout.fragment_playables), MainHeaderChild {
    private val controller: PlayablesController by viewModel()
    private val adapter: PlayablesAdapter by inject()

    override val mainHeaderParent: FragmentActivity get() = requireActivity()

    init {
        lifecycleScope.launchWhenCreated {
            applyInsets()
            playablesRecyclerView.adapter = adapter
        }

        lifecycleScope.launchWhenStarted {
            playablesRecyclerView.scrolledPastItemChanges(itemIndex = 0)
                .map { if (it) View.VISIBLE else View.GONE }
                .bind(to = playablesScrollButton::setVisibility)
                .launchIn(this)

            playablesScrollButton.clicks()
                .bind { playablesRecyclerView.smoothScrollUp() }
                .launchIn(this)

            adapter.interaction
                .filterIsInstance<PlayablesAdapterInteraction.Clicked>()
                .sample(500)
                .map { it.gameId to retrieveActivityBlurredScreenShot() }
                .bind { (id, screenshot) ->
                    DetailView.start(requireContext(), id, screenshot)
                }
                .launchIn(this)

            adapter.interaction
                .filterIsInstance<PlayablesAdapterInteraction.PlayedClicked>()
                .map { PlayablesController.Action.SetGamePlayed(it.gameId, it.played) }
                .bind(to = controller.action)
                .launchIn(this)

            layoutPlayablesError.errorButton.clicks()
                .map { PlayablesController.Action.ReloadGames }
                .bind(to = controller.action)
                .launchIn(this)

            controller.state.changesFrom { it.games }
                .bind { games ->
                    layoutPlayablesLoading.isVisible = games.loading // todo gets stuck
                    playablesRecyclerView.isVisible = games.successful && !games().isNullOrEmpty()
                    layoutPlayablesEmpty.isVisible = games.successful && games().isNullOrEmpty()
                    layoutPlayablesError.isVisible = games.failed

                    if (games is Data.Success) adapter.submitGames(games.value)
                }
                .launchIn(this)
        }
    }

    private fun applyInsets() {
        playablesScrollButton.doOnApplyWindowInsets { view, windowInsets, viewState ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(bottom = viewState.margins.bottom + windowInsets.systemWindowInsetBottom)
            }
        }

        afterMainHeaderMeasure { header ->
            listOf(
                playablesRecyclerView,
                layoutPlayablesLoading,
                layoutPlayablesEmpty,
                layoutPlayablesError
            ).forEach {
                it.doOnApplyWindowInsets { view, windowInsets, viewState ->
                    view.updatePadding(
                        top = windowInsets.systemWindowInsetTop + header.height + viewState.paddings.top,
                        bottom = windowInsets.systemWindowInsetBottom + viewState.paddings.bottom
                    )
                }
            }
        }
    }
}