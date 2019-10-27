package at.florianschuster.playables.core.remote

import at.florianschuster.playables.core.model.ClientInfo
import io.ktor.client.HttpClient
import io.ktor.client.features.DefaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter

interface RAWGApi {
    suspend fun search(query: String, page: Int = 1, pageSize: Int = 12): RemoteSearch
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

    override suspend fun search(query: String, page: Int, pageSize: Int): RemoteSearch =
        httpClient.get("${baseUrl}/games") {
            url {
                parameter("search", query)
                parameter("page", page)
                parameter("page_size", pageSize)
            }
        }

    override suspend fun game(id: Long): RemoteGame =
        httpClient.get("${baseUrl}/games/$id")
}