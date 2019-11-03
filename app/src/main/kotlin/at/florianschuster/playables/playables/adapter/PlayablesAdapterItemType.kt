package at.florianschuster.playables.playables.adapter

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.Game

sealed class PlayablesAdapterItemType(@LayoutRes val layoutRes: Int) {
    data class Header(
        val headerText: String
    ) : PlayablesAdapterItemType(R.layout.item_playables_header)

    data class Item(val game: Game) : PlayablesAdapterItemType(R.layout.item_playables)

    companion object {
        val itemDiff = object : DiffUtil.ItemCallback<PlayablesAdapterItemType>() {
            override fun areItemsTheSame(
                oldItem: PlayablesAdapterItemType,
                newItem: PlayablesAdapterItemType
            ): Boolean = oldItem.layoutRes == newItem.layoutRes

            override fun areContentsTheSame(
                oldItem: PlayablesAdapterItemType,
                newItem: PlayablesAdapterItemType
            ): Boolean = when {
                oldItem is Header && newItem is Header -> oldItem.headerText == newItem.headerText
                oldItem is Item && newItem is Item -> oldItem.game == newItem.game
                else -> false
            }
        }
    }
}