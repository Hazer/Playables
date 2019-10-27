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
    @SerialName("description_raw") val description: String,
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
internal data class RemotePlatform(val id: Long? = null, val name: String? = null)

@Serializable
internal data class RemoteStore(val id: Long? = null, val name: String? = null)

@Serializable
internal data class RemoteGenre(val id: Long? = null, val name: String? = null, val image: String? = null)

@Serializable
internal data class RemoteScreenShot(val id: Long? = null, val image: String? = null)

@Serializable
internal data class RemoteDeveloper(
    val id: Long? = null,
    val name: String? = null,
    @SerialName("image_background") val image: String? = null
)

@Serializable
internal data class RemoteTag(
    val id: Long? = null,
    val name: String? = null,
    @SerialName("image_background") val image: String? = null
)

@Serializable
internal data class RemoteRating(val id: Long? = null, val name: String? = null)