package at.florianschuster.playables.core

import at.florianschuster.playables.core.local.GamesDatabase
import at.florianschuster.playables.core.local.LocalGameData
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.core.model.Platform
import at.florianschuster.playables.core.provider.DispatcherProvider
import at.florianschuster.playables.core.remote.RemoteGamesApi
import at.florianschuster.playables.core.remote.RemoteGame
import at.florianschuster.playables.core.remote.RemotePlatform
import at.florianschuster.playables.core.remote.RemoteSearch
import at.florianschuster.playables.core.remote.RemoteTrailers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class GamesRepo(
    private val api: RemoteGamesApi,
    private val database: GamesDatabase,
    private val dispatcherProvider: DispatcherProvider
) {

    private val reloadMyGamesChannel = ConflatedBroadcastChannel(Unit)

    fun observe(gameId: Long): Flow<Game> = flow<Game> {
        val remoteGame = withTimeout(api_timeout) { api.game(gameId) }
        val remoteTrailer = withTimeout(api_timeout) { api.trailers(gameId) }
        emitAll(database.observe(gameId).map { remoteGame.asGame(it, remoteTrailer) })
    }.flowOn(dispatcherProvider.io)

    suspend fun search(
        query: String,
        page: Int
    ): List<Game> = withContext(dispatcherProvider.io) {
        val remoteSearch = withTimeout(api_timeout) {
            api.search(query, page).results
        }
        remoteSearch.map { it.asGame(database.get(it.id)) }
    }

    suspend fun add(game: Game) = database.insertOrUpdate(game.asLocalGameData())

    suspend fun remove(gameId: Long) = database.delete(gameId)

    fun reloadMyGames() {
        reloadMyGamesChannel.offer(Unit)
    }

    fun observeMyGames(): Flow<List<Game>> = flow<List<Game>> {
        val reloadFlow = reloadMyGamesChannel.asFlow().map { database.getAll() }
        val databaseFlow = database.observeAll().distinctUntilChanged()

        val gamesFlow = flowOf(reloadFlow, databaseFlow)
            .flattenMerge()
            .map { it.map(LocalGameData::asGame).sortedBy(Game::name) }
        emitAll(gamesFlow)
    }.flowOn(dispatcherProvider.io)

    suspend fun setPlayed(gameId: Long, played: Boolean) = database.update(gameId, played)

    companion object {
        const val api_timeout = 5_000L
    }
}

private fun RemoteSearch.Result.asGame(localGameData: LocalGameData?): Game {
    return Game(
        id = id,
        name = name,
        image = backgroundImage,
        releaseDate = released,
        platforms = platforms.asPlatforms(),
        added = localGameData != null,
        played = localGameData?.played ?: false
    )
}

private fun RemoteGame.asGame(
    localGameData: LocalGameData?,
    remoteTrailers: RemoteTrailers?
): Game {
    return Game(
        id = id,
        name = name,
        image = backgroundImage,
        description = description,
        website = website,
        releaseDate = releaseDate,
        trailers = remoteTrailers?.asGameTrailers() ?: emptyList(),
        platforms = platforms.asPlatforms(),
        added = localGameData != null,
        played = localGameData?.played ?: false
    )
}

private fun RemoteTrailers.asGameTrailers(): List<Game.Trailer> {
    return results.map { Game.Trailer(it.id, it.preview, it.data.quality480Url) }
}

private fun List<RemotePlatform>.asPlatforms(): List<Platform> {
    return map {
        when (it.platform.id) {
            4L -> Platform.PC
            5L -> Platform.MAC
            18L, // PS4
            16L, // PS3
            15L, // PS2
            27L // PS
            -> Platform.Playstation
            1L, // One
            14L, // 360
            80L // XBox
            -> Platform.XBox
            7L -> Platform.Switch
            21L, // Android
            3L // iOS
            -> Platform.Phone
            8L, // 3DS
            9L, // DS
            13L // DSi
            -> Platform.NintendoDS
            17L -> Platform.Web
            else -> Platform.Other
        }
    }.sortedBy(Platform::ordinal).distinct()
}

private fun Game.asLocalGameData(): LocalGameData {
    return object : LocalGameData {
        override val gameId: Long = id
        override val name: String = this@asLocalGameData.name
        override val imageUrl: String? = image
        override val platforms: List<Platform> = this@asLocalGameData.platforms
        override val played: Boolean = this@asLocalGameData.played
    }
}

private fun LocalGameData.asGame(): Game {
    return Game(
        gameId,
        name,
        imageUrl,
        platforms = platforms,
        played = played,
        added = true
    )
}
