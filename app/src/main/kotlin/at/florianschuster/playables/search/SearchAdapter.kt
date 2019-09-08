/*
 * Copyright 2019 Florian Schuster (https://florianschuster.at/).
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

package at.florianschuster.playables.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.SearchResult
import com.tailoredapps.androidutil.ui.extensions.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_search.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

class SearchAdapter : ListAdapter<SearchResult, SearchViewHolder>(diff) {
    var interaction: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(parent.inflate(R.layout.item_search))

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) =
        holder.bind(getItem(position)) { interaction?.invoke(it) }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean =
                oldItem == newItem
        }
    }
}

class SearchViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(searchResult: SearchResult, onClick: (Long) -> Unit) {
        nameTextView.text = searchResult.name
        containerView.setOnClickListener { onClick(searchResult.id) }
    }
}