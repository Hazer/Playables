package at.florianschuster.playables.playables

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseFragment
import at.florianschuster.playables.base.ui.doOnApplyWindowInsets
import at.florianschuster.playables.controller.Controller
import at.florianschuster.playables.controller.ControllerViewModel
import at.florianschuster.playables.controller.Data
import at.florianschuster.playables.controller.DefaultController
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.detail.startDetail
import com.tailoredapps.androidutil.ui.extensions.addScrolledPastItemListener
import com.tailoredapps.androidutil.ui.extensions.afterMeasured
import com.tailoredapps.androidutil.ui.extensions.toast
import kotlinx.android.synthetic.main.fragment_playables.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.ldralighieri.corbind.view.clicks

class PlayablesFragment : BaseFragment(layout = R.layout.fragment_playables) {
    private val viewModel: PlayablesViewModel by viewModel()
    private val adapter: PlayablesAdapter by inject()

    init {
        lifecycleScope.launchWhenStarted {
            playablesRecyclerView.adapter = adapter
            adapter.interaction = {
                when (it) {
                    is PlayablesAdapterInteraction.Clicked -> {
                        requireContext().startDetail(it.game.id)
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
                .onEach { playablesRecyclerView.smoothScrollToPosition(0) }
                .launchIn(this)

            viewModel.playables
                .distinctUntilChanged()
                .onEach { games ->
                    playablesRecyclerView.isVisible = !games().isNullOrEmpty()
                    layoutPlayablesEmpty.isVisible = games().isNullOrEmpty()

                    when (games) {
                        is Data.Success -> adapter.submitList(games.element)
                        is Data.Failure -> toast("Error todo")
                    }
                }
                .launchIn(this)
        }

        //insets
        lifecycleScope.launchWhenCreated {
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
}

class PlayablesViewModel(
    dataRepo: DataRepo
) : ViewModel() {
    val playables: Flow<Data<List<Game>>> = dataRepo.playables().map { Data.Success(it) }
}