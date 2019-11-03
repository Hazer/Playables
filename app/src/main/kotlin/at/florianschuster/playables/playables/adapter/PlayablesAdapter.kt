package at.florianschuster.playables.playables.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.util.inflate
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow

sealed class PlayablesAdapterInteraction {
    data class Clicked(val gameId: Long) : PlayablesAdapterInteraction()
    data class PlayedClicked(val gameId: Long, val played: Boolean) : PlayablesAdapterInteraction()
}

class PlayablesAdapter : ListAdapter<PlayablesAdapterItemType, PlayablesViewHolder>(
    PlayablesAdapterItemType.itemDiff
) {
    private val _interaction = BroadcastChannel<PlayablesAdapterInteraction>(1)
    val interaction = _interaction.asFlow()

    fun submitGames(games: List<Game>) {
        val items = when {
            games.count() >= 10 -> {
                games.sortedBy(Game::name)
                    .groupBy { it.name[0] }
                    .flatMap { (firstLetterOfName, games) ->
                        listOf(PlayablesAdapterItemType.Header(firstLetterOfName.toString())) +
                                games.map(PlayablesAdapterItemType::Item)
                    }
            }
            else -> games.sortedBy(Game::name).map(PlayablesAdapterItemType::Item)
        }
        submitList(items)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).layoutRes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayablesViewHolder =
        when (viewType) {
            R.layout.item_playables_header -> PlayablesViewHolder.Header(
                parent.inflate(viewType)
            )
            else -> PlayablesViewHolder.Item(
                parent.inflate(
                    viewType
                )
            )
        }

    override fun onBindViewHolder(holder: PlayablesViewHolder, position: Int) = when (holder) {
        is PlayablesViewHolder.Item -> {
            val item = getItem(position) as PlayablesAdapterItemType.Item
            holder.bind(item.game, _interaction)
        }
        is PlayablesViewHolder.Header -> {
            val item = getItem(position) as PlayablesAdapterItemType.Header
            holder.bind(item.headerText)
        }
    }
}
