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

import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.navigation.fragment.navArgs
import at.florianschuster.playables.R
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.base.ui.BaseFragment
import at.florianschuster.playables.base.ui.BaseViewModel
import at.florianschuster.playables.core.model.Game
import com.tailoredapps.androidutil.async.Async
import com.tailoredapps.androidutil.ui.extensions.argument
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@Parcelize
data class DetailFragmentArgs(val id: Long) : Parcelable

class DetailFragment : BaseFragment(R.layout.fragment_detail) {
    private val args: DetailFragmentArgs = DetailFragmentArgs(2) // todo
    private val viewModel: DetailViewModel by viewModel { parametersOf(args.id) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.game.observe(this, Observer { game ->
            progressBar.isVisible = game.loading
            when (game) {
                is Async.Success -> {
                    nameTextView.text = game.element.name
                    descriptionTextView.text = Html.fromHtml(game.element.description)
                }
                is Async.Error -> {
                    nameTextView.text = "Error: ${game.error}"
                }
            }
        })
    }
}

class DetailViewModel(
    private val gameId: Long,
    private val dataRepo: DataRepo
) : BaseViewModel() {
    val game: LiveData<Async<Game>> = liveData {
        emit(Async.Loading)
        try {
            emit(Async.success(dataRepo.game(gameId)))
        } catch (exception: Exception) {
            Timber.e(exception)
            emit(Async.error(exception))
        }
    }
}
