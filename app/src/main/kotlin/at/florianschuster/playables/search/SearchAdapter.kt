package at.florianschuster.playables.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.SearchResult
import at.florianschuster.playables.util.inflate
import coil.api.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_search.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.asFlow

sealed class SearchAdapterInteraction {
    data class Click(val gameId: Long) : SearchAdapterInteraction()
    data class AddGame(val game: SearchResult) : SearchAdapterInteraction()
}

class SearchAdapter : ListAdapter<SearchResult, SearchViewHolder>(diff) {
    private val _interaction = BroadcastChannel<SearchAdapterInteraction>(1)
    val interaction = _interaction.asFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(parent.inflate(R.layout.item_search))

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) =
        holder.bind(getItem(position), _interaction)

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
    fun bind(searchResult: SearchResult, interaction: SendChannel<SearchAdapterInteraction>) {
        cardView.setOnClickListener {
            interaction.offer(SearchAdapterInteraction.Click(searchResult.id))
        }
        nameTextView.text = searchResult.name
        with(gameImageView) {
            clipToOutline = true
            load(searchResult.image) { crossfade(true) }
        }
    }
}