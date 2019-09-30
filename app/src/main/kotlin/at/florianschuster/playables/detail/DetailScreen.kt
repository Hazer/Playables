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

package at.florianschuster.playables.detail

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseActivity
import at.florianschuster.playables.base.ui.BaseViewModel
import at.florianschuster.playables.base.ui.doOnApplyWindowInsets
import at.florianschuster.playables.controller.Data
import at.florianschuster.playables.controller.bind
import at.florianschuster.playables.controller.filterDataSuccess
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.util.openChromeTab
import coil.api.load
import coil.transform.BlurTransformation
import com.tailoredapps.androidutil.ui.extensions.extra
import com.tailoredapps.androidutil.ui.extensions.extras
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.ldralighieri.corbind.view.clicks
import java.io.File
import kotlin.math.max

private const val EXTRA_ID = "gameId"
private const val EXTRA_BACKGROUND_FILE = "bg_file"

fun Context.startDetail(id: Long, backGroundFile: File?) {
    val intent = Intent(this, DetailActivity::class.java)
        .extras(EXTRA_ID to id)
        .apply { if (backGroundFile != null) extras(EXTRA_BACKGROUND_FILE to backGroundFile) }
    startActivity(intent)
}

//todo refactor to bottomsheet
class DetailActivity : BaseActivity(layout = R.layout.activity_detail) {
    private val id: Long by extra(EXTRA_ID)
    private val backgroundFile: File? by extra(EXTRA_BACKGROUND_FILE)
    private val viewModel: DetailViewModel by viewModel { parametersOf(id) }

    init {
        lifecycleScope.launchWhenStarted {
            gameImageView.clipToOutline = true

            backgroundImageView.load(backgroundFile)

            detailContent.onDismissed = { finish() }
            detailContent.onDragOffset = { direction, percent ->
                when (direction) {
                    DragDirection.DOWN -> max(0f, 1 - (percent * 2))
                    else -> 1f
                }.let(backgroundImageView::setAlpha)

                when (direction) {
                    DragDirection.DOWN -> max(0f, 1 - (percent * 1.75f))
                    else -> 1f
                }.let {
                    nameTextView.alpha = it
                    platforms.alpha = it
                    descriptionTextView.alpha = it
                    websiteButton.alpha = it
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

            viewModel.game.distinctUntilChanged()
                .bind { game ->
                    loadingProgressBar.isVisible = game.loading
                    when (game) {
                        is Data.Success -> {
                            gameImageView.load(game.element.image) { crossfade(true) }
                            if (backgroundFile == null) {
                                backgroundImageView.load(game.element.image) {
                                    crossfade(true)
                                    transformations(
                                        BlurTransformation(this@DetailActivity, 25f, 5f)
                                    )
                                }
                            }
                            nameTextView.text = game.element.name
                            descriptionTextView.text = game.element.description
                            websiteButton.isVisible = !game.element.website.isNullOrEmpty()
                        }
                        is Data.Failure -> {
                            nameTextView.text = "Error: ${game.error}"
                        }
                    }
                }
                .launchIn(this)

            websiteButton.clicks()
                .map { viewModel.latestGame }
                .filterNotNull()
                .filterDataSuccess()
                .bind { openChromeTab(it.website) }
                .launchIn(this)
        }

        //fullscreen + insets
        lifecycleScope.launchWhenCreated {
            detailContent.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            window.statusBarColor = resources.getColor(R.color.transparent, null)

            detailContentConstraintLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding, _ ->
                view.updatePadding(
                    top = initialPadding.top + windowInsets.systemWindowInsetTop
                )
            }

            websiteButton.doOnApplyWindowInsets { view, windowInsets, _, initialMargin ->
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(
                        bottom = initialMargin.bottom + windowInsets.systemWindowInsetBottom
                    )
                }
            }
        }
    }
}

class DetailViewModel(
    private val gameId: Long,
    private val dataRepo: DataRepo
) : BaseViewModel() {
    @Deprecated("Replace with stateFlow: https://github.com/Kotlin/kotlinx.coroutines/issues/1082")
    var latestGame: Data<Game>? = null

    @Deprecated("Replace with stateFlow: https://github.com/Kotlin/kotlinx.coroutines/issues/1082")
    val game: Flow<Data<Game>> = flow {
        emit(Data.Loading)
        val game = Data.of { dataRepo.game(gameId) }
        latestGame = game
        emit(game)
    }
}
