package at.florianschuster.playables.core

import at.florianschuster.playables.core.local.Database
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.core.model.SearchResult
import at.florianschuster.playables.core.remote.RAWGApi
import at.florianschuster.playables.core.remote.RemoteGame
import at.florianschuster.playables.core.remote.RemoteSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

interface DataRepo {
    suspend fun search(query: String, page: Int): List<SearchResult>
    suspend fun game(id: Long): Game

    fun reloadPlayables()
    fun playables(): Flow<List<Game>>
}

class CoreDataRepo(
    private val api: RAWGApi,
    private val database: Database
) : DataRepo {

    private val reloadPlayablesChannel = ConflatedBroadcastChannel(Unit)

    override suspend fun search(
        query: String,
        page: Int
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        withTimeout(5000) {
            api.search(query, page).results.map { it.asSearchResult() }
        }
    }

    override suspend fun game(id: Long): Game = withContext(Dispatchers.IO) {
        withTimeout(5000) {
            api.game(id).asGame()
        }
    }

    override fun reloadPlayables() {
        reloadPlayablesChannel.offer(Unit)
    }

    override fun playables(): Flow<List<Game>> = flow {
        val gameIds = listOf(3498L, 802L, 12020L)
        reloadPlayablesChannel.asFlow()
            .map { gameIds.map { game(it) }.sortedBy(Game::name) }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    private fun RemoteSearch.Result.asSearchResult(): SearchResult {
        return SearchResult(id, name, backgroundImage)
    }

    private fun RemoteGame.asGame(): Game {
        return Game(id, name, description, backgroundImage, website, released.toEpochDay())
    }
}
