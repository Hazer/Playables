/*
 * Copyright 2019 Florian Schuster (https://florianschuster.at/).
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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDate

@Serializable
data class RemoteSearch(
    val count: Long,
    val next: String?,
    val previous: String?,
    val results: List<Result>
) {
    @Serializable
    data class Result(
        val id: Long,
        val name: String,
        val playtime: Long,
//        val platforms: List<RemotePlatform>,
//        val stores: List<RemoteStore>,
        val released: @Serializable(with = LocalDateSerializer::class) LocalDate? = null,
        val tba: Boolean,
        @SerialName("background_image") val backgroundImage: String? = null,
        @SerialName("suggestions_count") val suggestionsCount: Long,
        @SerialName("metacritic_url") val metaCriticURL: String? = null
//        @SerialName("short_screenshots") val screenshots: List<RemoteScreenShot>,
//        val genres: List<RemoteGenre>
    )
}

@Serializable
data class RemoteGame(
    val id: Long,
    val name: String,
    @SerialName("description") val descriptionHtml: String,
    val released: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val tba: Boolean,
    @SerialName("background_image") val backgroundImage: String,
    val website: String,
    val playtime: Long,
    @SerialName("suggestions_count") val suggestionsCount: Long,
    @SerialName("saturated_color") val saturatedColor: String,
    @SerialName("dominant_color") val dominantColor: String
//    val platforms: List<RemotePlatform>,
//    val developers: List<RemoteDeveloper>,
//    val genres: List<RemoteGenre>,
//    val tags: List<RemoteTag>,
//    val publishers: List<RemoteDeveloper>,
//    @SerialName("esrb_rating") val esrbRating: RemoteRating
)

@Serializable
data class RemotePlatform(val id: Long? = null, val name: String? = null)

@Serializable
data class RemoteStore(val id: Long? = null, val name: String? = null)

@Serializable
data class RemoteGenre(val id: Long? = null, val name: String? = null, val image: String? = null)

@Serializable
data class RemoteScreenShot(val id: Long? = null, val image: String? = null)

@Serializable
data class RemoteDeveloper(
    val id: Long? = null,
    val name: String? = null,
    @SerialName("image_background") val image: String? = null
)

@Serializable
data class RemoteTag(
    val id: Long? = null,
    val name: String? = null,
    @SerialName("image_background") val image: String? = null
)

@Serializable
data class RemoteRating(val id: Long? = null, val name: String? = null)