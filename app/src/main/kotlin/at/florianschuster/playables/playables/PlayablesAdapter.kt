package at.florianschuster.playables.playables

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.util.PlatformsView
import at.florianschuster.playables.util.inflate
import coil.api.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_playables.*
import kotlinx.android.synthetic.main.item_playables.cardView
import kotlinx.android.synthetic.main.item_playables.gameImageView
import kotlinx.android.synthetic.main.item_playables.nameTextView
import kotlinx.android.synthetic.main.item_playables.platformsView
import kotlinx.android.synthetic.main.item_search.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.asFlow

sealed class PlayablesAdapterInteraction {
    data class Clicked(val gameId: Long) : PlayablesAdapterInteraction()
    data class PlayedClicked(val gameId: Long, val played: Boolean) :
        PlayablesAdapterInteraction()
}

class PlayablesAdapter : ListAdapter<Game, PlayableViewHolder>(diff) {
    private val _interaction = BroadcastChannel<PlayablesAdapterInteraction>(1)
    val interaction = _interaction.asFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayableViewHolder =
        PlayableViewHolder(parent.inflate(R.layout.item_playables))

    override fun onBindViewHolder(holder: PlayableViewHolder, position: Int) =
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

class PlayableViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(game: Game, interaction: SendChannel<PlayablesAdapterInteraction>) {
        cardView.setOnClickListener {
            interaction.offer(PlayablesAdapterInteraction.Clicked(game.id))
        }
        nameTextView.text = game.name
        with(gameImageView) {
            clipToOutline = true
            load(game.image) { crossfade(true) }
        }
        with(playedButton) {
            setText(if (game.played) R.string.game_played else R.string.game_not_played)
            setOnClickListener {
                interaction.offer(PlayablesAdapterInteraction.PlayedClicked(game.id, !game.played))
            }
        }
        with(platformsView) {
            isVisible = game.platforms.isNotEmpty()
            setPlatforms(game.platforms, PlatformsView.Style.Small)
        }
    }
}