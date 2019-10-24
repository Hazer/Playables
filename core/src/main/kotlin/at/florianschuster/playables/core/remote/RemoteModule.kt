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
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.SIMPLE
import kotlinx.serialization.json.Json
import org.koin.dsl.module

internal val remoteModule = module {
    single { provideHttpClient(json = get(), clientInfo = get()) }
    single<RAWGApi> { HttpClientRAWGApi(httpClient = get(), clientInfo = get()) }
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