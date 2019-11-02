package at.florianschuster.playables.core.android.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import at.florianschuster.playables.core.local.GamesDatabase
import at.florianschuster.playables.core.local.LocalGameData
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.core.model.Platform
import kotlinx.coroutines.flow.Flow

internal class RoomGamesDatabase(
    context: Context
) : GamesDatabase {

    private val dao: RoomLocalGameDataDao by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "playables_database")
            .build()
            .localGameDataDao()
    }

    override suspend fun insertOrUpdate(data: LocalGameData) = dao.insertOrReplace(data.asRoom())
    override suspend fun update(gameId: Long, played: Boolean) {
        val game = dao.get(gameId) ?: return
        dao.insertOrReplace(game.copy(played = played))
    }
    override fun observe(gameId: Long): Flow<LocalGameData?> = dao.observe(gameId)
    override suspend fun get(gameId: Long): LocalGameData? = dao.get(gameId)
    override fun observeAll(): Flow<List<LocalGameData>> = dao.observeAll()
    override suspend fun getAll(): List<LocalGameData> = dao.getAll()
    override suspend fun delete(gameId: Long) = dao.delete(gameId)

    private fun LocalGameData.asRoom(): RoomLocalGameData =
        RoomLocalGameData(gameId, name, imageUrl, platforms, played)
}

@Dao
internal interface RoomLocalGameDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(data: RoomLocalGameData)

    @Query("SELECT * FROM local_game_data WHERE gameId=:gameId")
    fun observe(gameId: Long): Flow<RoomLocalGameData?>

    @Query("SELECT * FROM local_game_data WHERE gameId=:gameId")
    suspend fun get(gameId: Long): RoomLocalGameData?

    @Query("SELECT * FROM local_game_data")
    suspend fun getAll(): List<RoomLocalGameData>

    @Query("SELECT * FROM local_game_data")
    fun observeAll(): Flow<List<RoomLocalGameData>>

    @Query("DELETE FROM local_game_data WHERE gameId = :gameId")
    suspend fun delete(gameId: Long)
}