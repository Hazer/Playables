/*
 * Copyright 2019 Florian Schuster.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.florianschuster.playables.core

import at.florianschuster.playables.core.local.Database
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.core.model.SearchResult
import at.florianschuster.playables.core.remote.RAWGApi
import at.florianschuster.playables.core.remote.RemoteGame
import at.florianschuster.playables.core.remote.RemoteSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

interface DataRepo {
    suspend fun search(query: String, page: Int): List<SearchResult>

    suspend fun game(id: Long): Game

    fun playables(): Flow<List<Game>>
}

class CoreDataRepo(
    private val api: RAWGApi,
    private val database: Database
) : DataRepo {

    override suspend fun search(
        query: String,
        page: Int
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        api.search(query, page).results.map { it.asSearchResult() }
    }

    override suspend fun game(id: Long): Game = withContext(Dispatchers.IO) {
        api.game(id).asGame()
    }

    override fun playables(): Flow<List<Game>> = flow {
        val games = listOf(3498L, 802L, 12020L)
            .map { api.game(it).asGame() }
            .sortedBy(Game::name)
        emit(games)
    }

    private fun RemoteSearch.Result.asSearchResult(): SearchResult {
        return SearchResult(id, name, backgroundImage)
    }

    private fun RemoteGame.asGame(): Game {
        return Game(id, name, descriptionHtml, backgroundImage)
    }
}
