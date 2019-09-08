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

import at.florianschuster.playables.R
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.base.ui.BaseFragment
import at.florianschuster.playables.base.ui.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : BaseFragment(R.layout.fragment_detail) {
    val viewModel: DetailViewModel by viewModel()

}

class DetailViewModel(
    private val dataRepo: DataRepo
) : BaseViewModel() {
}
