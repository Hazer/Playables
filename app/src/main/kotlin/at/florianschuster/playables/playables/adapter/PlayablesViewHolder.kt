package at.florianschuster.playables.playables.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.util.PlatformsView
import coil.api.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_playables.*
import kotlinx.android.synthetic.main.item_playables_header.*
import kotlinx.coroutines.channels.SendChannel

sealed class PlayablesViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    class Header(containerView: View) : PlayablesViewHolder(containerView) {
        fun bind(headerText: String) {
            headerTextView.text = headerText
        }
    }

    class Item(containerView: View) : PlayablesViewHolder(containerView) {
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
                isSelected = game.played

                playedButton.setImageResource(
                    if (game.played) R.drawable.ic_controller_filled else R.drawable.ic_controller
                )

                setOnClickListener {
                    interaction.offer(
                        PlayablesAdapterInteraction.PlayedClicked(game.id, !game.played)
                    )
                }
            }
            with(platformsView) {
                isVisible = game.platforms.isNotEmpty()
                setPlatforms(game.platforms, PlatformsView.Style.Small)
            }
        }
    }
}