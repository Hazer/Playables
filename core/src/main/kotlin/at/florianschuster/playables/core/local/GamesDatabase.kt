package at.florianschuster.playables.core.local

import kotlinx.coroutines.flow.Flow

interface GamesDatabase {
    suspend fun insertOrUpdate(data: LocalGameData)
    suspend fun update(gameId: Long, played: Boolean)
    fun observe(gameId: Long): Flow<LocalGameData?>
    suspend fun get(gameId: Long): LocalGameData?
    fun observeAll(): Flow<List<LocalGameData>>
    suspend fun getAll(): List<LocalGameData>
    suspend fun delete(gameId: Long)
}