package at.florianschuster.playables.detail

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import at.florianschuster.control.bind
import at.florianschuster.control.changesFrom
import at.florianschuster.data.lce.Data
import at.florianschuster.data.lce.filterSuccessData
import at.florianschuster.playables.R
import at.florianschuster.playables.base.BaseActivity
import at.florianschuster.playables.util.displayString
import at.florianschuster.playables.util.openChromeTab
import coil.api.load
import com.tailoredapps.androidutil.ui.extensions.extra
import com.tailoredapps.androidutil.ui.extensions.extras
import com.tailoredapps.androidutil.ui.extensions.toast
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import java.io.File
import java.time.LocalDate
import kotlin.math.max

@Parcelize
private data class DetailArgs(val id: Long, val backGroundFile: File?) : Parcelable {
    companion object {
        const val KEY = "detail_args"
    }
}

class DetailView : BaseActivity(layout = R.layout.activity_detail) {

    private val args: DetailArgs by extra(DetailArgs.KEY)
    private val controller: DetailController by viewModel { parametersOf(args.id) }

    init {
        lifecycleScope.launchWhenCreated {
            applyInsets()
            backgroundImageView.load(args.backGroundFile)

            detailContent.onDismissed = { finish() }
            detailContent.onDragOffset = ::onDrag

            gamePlayerView.setOnPreparedListener { it.isLooping = true }
        }

        lifecycleScope.launchWhenStarted {
            websiteButton.clicks()
                .map { controller.currentState.game }
                .filterSuccessData()
                .map { it.website }
                .filterNotNull()
                .bind { openChromeTab(it) }
                .launchIn(scope = this)

            playedButton.clicks()
                .map { controller.currentState.game }
                .filterSuccessData()
                .map { game ->
                    if (game.played) {
                        DetailController.Action.SetGameNotPlayed
                    } else {
                        DetailController.Action.SetGamePlayed
                    }
                }
                .bind(to = controller.action)
                .launchIn(scope = this)

            flowOf(gamePlayerView.clicks(), gameImageView.clicks(), videoPlayButton.clicks())
                .flattenMerge()
                .map { controller.currentState.game }
                .filterSuccessData()
                .filter { it.trailers.isNotEmpty() }
                .bind {
                    videoPlayButton.isVisible = gamePlayerView.isPlaying
                    gameImageView.visibility = when {
                        gamePlayerView.isPlaying -> View.VISIBLE
                        else -> View.INVISIBLE
                    }
                    if (gamePlayerView.isPlaying) gamePlayerView.pause() else gamePlayerView.start()
                }
                .launchIn(scope = this)

            addRemoveButton.clicks()
                .map { controller.currentState.game }
                .filterSuccessData()
                .map { game ->
                    if (game.added) {
                        DetailController.Action.RemoveGame
                    } else {
                        DetailController.Action.AddGame
                    }
                }
                .bind(to = controller.action)
                .launchIn(scope = this)

            controller.state.changesFrom { it.game }
                .bind { game ->
                    loadingProgressBar.isVisible = game.loading
                    when (game) {
                        is Data.Success -> {
                            gameImageView.load(game.value.image) { crossfade(true) }
                            nameTextView.text = game.value.name
                            with(releasedTextView) {
                                val date = game.value.releaseDate
                                isVisible = date != null && date.isAfter(LocalDate.now())
                                text = date?.displayString
                            }
                            descriptionTextView.text = game.value.description
                            websiteButton.isVisible = !game.value.website.isNullOrEmpty()
                            with(platformsView) {
                                isVisible = game.value.platforms.isNotEmpty()
                                setPlatforms(game.value.platforms)
                            }
                            with(addRemoveButton) {
                                setText(if (game.value.added) R.string.game_remove else R.string.game_add)
                                backgroundTintList = when {
                                    game.value.added -> ColorStateList.valueOf(getColor(R.color.filledButtonRemove))
                                    else -> ColorStateList.valueOf(getColor(R.color.filledButtonAdd))
                                }
                            }
                            playedButton.setText(if (game.value.played) R.string.game_played else R.string.game_not_played)

                            val trailers = game.value.trailers
                            videoPlayButton.isVisible = trailers.isNotEmpty()
                            if (trailers.isNotEmpty()) {
                                gamePlayerView.setVideoURI(Uri.parse(trailers.random().videoUrl))
                            }
                        }
                        is Data.Failure -> {
                            toast("Error: ${game.error}")
                            finish()
                        }
                    }
                }
                .launchIn(scope = this)
        }
    }

    private fun applyInsets() {
        detailContent.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        window.statusBarColor = resources.getColor(R.color.transparent, null)

        detailContentConstraintLayout.doOnApplyWindowInsets { view, windowInsets, viewState ->
            view.updatePadding(
                top = viewState.paddings.top + windowInsets.systemWindowInsetTop
            )
        }

        websiteButton.doOnApplyWindowInsets { view, windowInsets, viewState ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(
                    bottom = viewState.margins.bottom + windowInsets.systemWindowInsetBottom
                )
            }
        }
    }

    private fun onDrag(direction: DragDirection, percent: Float) {
        when (direction) {
            DragDirection.DOWN -> max(0f, 1 - (percent * 2.25f))
            else -> 1f
        }.let(backgroundImageView::setAlpha)

        when (direction) {
            DragDirection.DOWN -> max(0f, 1 - (percent * 1.75f))
            else -> 1f
        }.let { alpha ->
            listOf(
                descriptionTextView,
                websiteButton
            ).forEach { it.alpha = alpha }
        }

        when (direction) {
            DragDirection.DOWN -> max(0f, 1 - percent)
            else -> 1f
        }.let(backgroundCard::setAlpha)

        when (direction) {
            DragDirection.DOWN -> max(0f, 1 - (percent * 0.25f))
            else -> 1f
        }.let(detailHeaderContentLayout::setAlpha)

        detailHeaderContentLayout.translationY = when (direction) {
            DragDirection.DOWN -> gameImageView.height * (percent * 0.5f)
            else -> 0f
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gamePlayerView.stopPlayback()
    }

    companion object {
        fun start(context: Context, id: Long, backGroundFile: File?) {
            Intent(context, DetailView::class.java)
                .extras(DetailArgs.KEY to DetailArgs(id, backGroundFile))
                .let(context::startActivity)
        }
    }
}
