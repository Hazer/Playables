package at.florianschuster.playables.core.android.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import at.florianschuster.playables.core.local.LocalGameData
import at.florianschuster.playables.core.model.Platform

@Entity(tableName = "local_game_data")
data class RoomLocalGameData(
    @PrimaryKey override val gameId: Long,
    override val name: String,
    override val imageUrl: String? = null,
    override val platforms: List<Platform>,
    override val played: Boolean
) : LocalGameData