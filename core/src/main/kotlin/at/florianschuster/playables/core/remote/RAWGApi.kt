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

package at.florianschuster.playables.core.remote

import at.florianschuster.playables.core.model.ClientInfo
import io.ktor.client.HttpClient
import io.ktor.client.features.DefaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter

interface RAWGApi {
    suspend fun search(query: String, page: Int = 1): RemoteSearch
    suspend fun game(id: Long): RemoteGame
}

class HttpClientRAWGApi(
    private val httpClient: HttpClient,
    clientInfo: ClientInfo
) : RAWGApi {
    private val baseUrl = "https://api.rawg.io/api"

    init {
        val userAgent = "${clientInfo.appName}/${clientInfo.version.code} ${clientInfo.userAgent}"
        httpClient.config {
            install(DefaultRequest) {
                headers { append("User-Agent", userAgent) }
            }
        }
    }

    override suspend fun search(query: String, page: Int): RemoteSearch =
        httpClient.get("${baseUrl}/games") {
            url {
                parameter("search", query)
                parameter("page", page)
                parameter("page_size", 20)
            }
        }

    override suspend fun game(id: Long): RemoteGame =
        httpClient.get("${baseUrl}/games/$id")
}