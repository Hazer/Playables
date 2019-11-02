package at.florianschuster.playables.core

import at.florianschuster.playables.core.model.ClientInfo
import at.florianschuster.playables.core.remote.HttpClientRemoteGamesApi
import at.florianschuster.playables.core.remote.RemoteGamesApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.SIMPLE
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val coreModule = module {
    single { Json.nonstrict }

    single { provideHttpClient(json = get(), clientInfo = get()) }
    single<RemoteGamesApi> { HttpClientRemoteGamesApi(httpClient = get(), clientInfo = get()) }

    single {
        GamesRepo(
            api = get(),
            database = get(),
            dispatcherProvider = get()
        )
    }
}

private fun provideHttpClient(
    json: Json,
    clientInfo: ClientInfo
) = HttpClient(engineFactory = CIO) {
    install(feature = JsonFeature) { serializer = KotlinxSerializer(json = json) }
    if (clientInfo.debug) {
        install(feature = Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }
}