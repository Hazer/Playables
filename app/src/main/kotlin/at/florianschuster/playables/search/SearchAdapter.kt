package at.florianschuster.playables.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.util.inflate
import coil.api.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_search.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.asFlow

sealed class SearchAdapterInteraction {
    data class Select(val gameId: Long) : SearchAdapterInteraction()
    data class Add(val game: Game) : SearchAdapterInteraction()
}

class SearchAdapter : ListAdapter<Game, SearchViewHolder>(diff) {
    private val _interaction = BroadcastChannel<SearchAdapterInteraction>(1)
    val interaction = _interaction.asFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(parent.inflate(R.layout.item_search))

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) =
        holder.bind(getItem(position), _interaction)

    companion object {
        private val diff = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean =
                oldItem == newItem
        }
    }
}

class SearchViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(game: Game, interaction: SendChannel<SearchAdapterInteraction>) {
        cardView.setOnClickListener {
            interaction.offer(SearchAdapterInteraction.Select(game.id))
        }
        nameTextView.text = game.name
        with(gameImageView) {
            clipToOutline = true
            load(game.image) { crossfade(true) }
        }
    }
}