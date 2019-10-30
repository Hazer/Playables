package at.florianschuster.playables.core

import at.florianschuster.playables.core.local.GamesDatabase
import at.florianschuster.playables.core.local.LocalGameData
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.core.provider.DispatcherProvider
import at.florianschuster.playables.core.remote.RawgApi
import at.florianschuster.playables.core.remote.RemoteGame
import at.florianschuster.playables.core.remote.RemoteSearch
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

interface DataRepo {
    suspend fun get(gameId: Long): Game

    suspend fun search(query: String, page: Int): List<Game>
    suspend fun add(game: Game)
    suspend fun delete(gameId: Long)

    fun reloadMyGames()
    fun observeMyGames(): Flow<List<Game>>
    suspend fun setPlayed(gameId: Long, played: Boolean)
}

class CoreDataRepo(
    private val api: RawgApi,
    private val database: GamesDatabase,
    private val dispatcherProvider: DispatcherProvider
) : DataRepo {

    private val reloadPlayablesChannel = ConflatedBroadcastChannel(Unit)

    override suspend fun get(gameId: Long): Game = withContext(dispatcherProvider.io) {
        val remoteGame = withTimeout(api_timeout) { api.game(gameId) }
        remoteGame.asGame(database.get(gameId))
    }

    override suspend fun search(
        query: String,
        page: Int
    ): List<Game> = withContext(dispatcherProvider.io) {
        val remoteSearchResults = withTimeout(api_timeout) { api.search(query, page).results }
        remoteSearchResults.map { it.asGame(database.get(it.id)) }

    }

    override suspend fun add(game: Game) = database.insertOrUpdate(game.asLocalGameData())

    override suspend fun delete(gameId: Long) = database.delete(gameId)

    override fun reloadMyGames() {
        reloadPlayablesChannel.offer(Unit)
    }

    override fun observeMyGames(): Flow<List<Game>> =
        flowOf(reloadPlayablesChannel.asFlow().map { database.getAll() }, database.observeAll())
            .flattenMerge()
            .distinctUntilChanged()
            .map { it.map { get(it.gameId) }.sortedBy(Game::name) }
            .flowOn(dispatcherProvider.io)

    override suspend fun setPlayed(gameId: Long, played: Boolean) = database.insertOrUpdate(
        object : LocalGameData {
            override val gameId: Long = gameId
            override val played: Boolean = played
        }
    )

    private fun RemoteSearch.Result.asGame(localGameData: LocalGameData?): Game {
        return Game(
            id, name,
            "",
            backgroundImage,
            "",
            released?.toEpochDay() ?: 0L,
            localGameData != null,
            localGameData?.played ?: false
        )
    }

    private fun RemoteGame.asGame(localGameData: LocalGameData?): Game {
        return Game(
            id,
            name,
            description,
            backgroundImage,
            website,
            released.toEpochDay(),
            localGameData != null,
            localGameData?.played ?: false
        )
    }

    private fun Game.asLocalGameData(): LocalGameData {
        return object : LocalGameData {
            override val gameId: Long = id
            override val played: Boolean = this@asLocalGameData.played
        }
    }

    companion object {
        const val api_timeout = 5_000L
    }
}
