package at.florianschuster.playables.playables

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.Game
import coil.api.load
import com.tailoredapps.androidutil.ui.extensions.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_playables.*

sealed class PlayablesAdapterInteraction {
    data class Clicked(val game: Game) : PlayablesAdapterInteraction()
    data class PlayedClicked(val game: Game, val played: Boolean) :
        PlayablesAdapterInteraction()
}

class PlayablesAdapter : ListAdapter<Game, PlayableViewHolder>(diff) {
    var interaction: ((PlayablesAdapterInteraction) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayableViewHolder =
        PlayableViewHolder(parent.inflate(R.layout.item_playables))

    override fun onBindViewHolder(holder: PlayableViewHolder, position: Int) =
        holder.bind(getItem(position)) { interaction?.invoke(it) }

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

    fun bind(game: Game, onClick: (PlayablesAdapterInteraction) -> Unit) {
        cardView.setOnClickListener {
            onClick(PlayablesAdapterInteraction.Clicked(game))
        }
        nameTextView.text = game.name
        with(gameImageView) {
            clipToOutline = true
            load(game.image) { crossfade(true) }
        }
        playedButton.isSelected = false
        playedButton.setOnClickListener {
            playedButton.isSelected = !playedButton.isSelected
            onClick(PlayablesAdapterInteraction.PlayedClicked(game, false))
        }
    }
}