package at.florianschuster.playables.detail

import android.content.Context
import android.content.Intent
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
import at.florianschuster.playables.util.openChromeTab
import coil.api.load
import coil.transform.BlurTransformation
import com.tailoredapps.androidutil.ui.extensions.extra
import com.tailoredapps.androidutil.ui.extensions.extras
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.ldralighieri.corbind.view.clicks
import java.io.File
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
        lifecycleScope.launchWhenCreated { applyInsets() }

        lifecycleScope.launchWhenStarted {
            gameImageView.clipToOutline = true

            backgroundImageView.load(args.backGroundFile)

            detailContent.onDismissed = { finish() }
            detailContent.onDragOffset = { direction, percent ->
                when (direction) {
                    DragDirection.DOWN -> max(0f, 1 - (percent * 2))
                    else -> 1f
                }.let(backgroundImageView::setAlpha)

                when (direction) {
                    DragDirection.DOWN -> max(0f, 1 - (percent * 1.75f))
                    else -> 1f
                }.let { alpha ->
                    listOf(
                        detailHeaderContentLayout,
                        descriptionTextView,
                        websiteButton
                    ).forEach { it.alpha = alpha }
                }

                when (direction) {
                    DragDirection.DOWN -> max(0f, 1 - percent)
                    else -> 1f
                }.let(backgroundCard::setAlpha)

                gameImageView.translationY = when (direction) {
                    DragDirection.DOWN -> gameImageView.height * (percent * 0.5f)
                    else -> 0f
                }
            }

            controller.state.changesFrom { it.game }
                .bind { game ->
                    loadingProgressBar.isVisible = game.loading
                    when (game) {
                        is Data.Success -> {
                            gameImageView.load(game.value.image) { crossfade(true) }
                            if (args.backGroundFile == null) {
                                backgroundImageView.load(game.value.image) {
                                    crossfade(true)
                                    transformations(
                                        BlurTransformation(this@DetailView, 25f, 5f)
                                    )
                                }
                            }
                            nameTextView.text = game.value.name
                            releasedTextView.text = game.value.releaseDateInUnix.toString()
                            descriptionTextView.text = game.value.description
                            websiteButton.isVisible = !game.value.website.isNullOrEmpty()
                        }
                        is Data.Failure -> {
                            nameTextView.text = "Error: ${game.error}"
                        }
                    }
                }
                .launchIn(this)

            websiteButton.clicks()
                .map { controller.currentState.game }
                .filterSuccessData()
                .bind { openChromeTab(it.website) }
                .launchIn(this)
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

    companion object {
        fun start(context: Context, id: Long, backGroundFile: File?) {
            Intent(context, DetailView::class.java)
                .extras(DetailArgs.KEY to DetailArgs(id, backGroundFile))
                .let(context::startActivity)
        }
    }
}