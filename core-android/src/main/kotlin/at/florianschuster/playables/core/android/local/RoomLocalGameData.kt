package at.florianschuster.playables.core.android.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import at.florianschuster.playables.core.local.LocalGameData

@Entity(tableName = "local_game_data")
data class RoomLocalGameData(
    @PrimaryKey override val gameId: Long,
    override val played: Boolean
) : LocalGameData