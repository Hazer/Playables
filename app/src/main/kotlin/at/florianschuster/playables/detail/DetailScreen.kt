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
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.lifecycle.lifecycleScope
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseActivity
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.base.ui.BaseViewModel
import at.florianschuster.playables.base.ui.doOnApplyWindowInsets
import at.florianschuster.playables.controller.Data
import at.florianschuster.playables.core.model.Game
import coil.api.load
import coil.transform.BlurTransformation
import com.tailoredapps.androidutil.ui.extensions.extra
import com.tailoredapps.androidutil.ui.extensions.extras
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_ID = "gameId"

fun Context.startDetail(id: Long) {
    val intent = Intent(this, DetailActivity::class.java)
        .extras(EXTRA_ID to id)
    startActivity(intent)
}

class DetailActivity : BaseActivity(layout = R.layout.activity_detail) {
    private val id: Long by extra(EXTRA_ID)
    private val viewModel: DetailViewModel by viewModel { parametersOf(id) }

    init {
        lifecycleScope.launchWhenStarted {
            closeButton.setOnClickListener { finish() }
            gameImageView.clipToOutline = true

            viewModel.game.distinctUntilChanged()
                .onEach { game ->
                    loadingProgressBar.isVisible = game.loading
                    when (game) {
                        is Data.Success -> {
                            gameImageView.load(game.element.image) { crossfade(true) }
                            backgroundImageView.load(game.element.image) {
                                crossfade(true)
                                transformations(BlurTransformation(this@DetailActivity, 25f, 5f))
                            }
                            nameTextView.text = game.element.name
                            descriptionTextView.text = Html.fromHtml(game.element.description)
                        }
                        is Data.Failure -> {
                            nameTextView.text = "Error: ${game.error}"
                        }
                    }
                }
                .launchIn(this)
        }

        //fullscreen + insets
        lifecycleScope.launchWhenCreated {
            scrollDetail.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            contentConstraintLayout.doOnApplyWindowInsets { view, windowInsets, _, initialMargin ->
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(top = initialMargin.top + windowInsets.systemWindowInsetTop)
                }
            }
        }
    }
}

class DetailViewModel(
    private val gameId: Long,
    private val dataRepo: DataRepo
) : BaseViewModel() {
    val game: Flow<Data<Game>> = flow {
        emit(Data.Loading)
        emit(Data.of { dataRepo.game(gameId) })
    }
}
