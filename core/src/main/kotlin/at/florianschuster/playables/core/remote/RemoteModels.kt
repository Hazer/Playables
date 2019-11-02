package at.florianschuster.playables.core.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class RemoteSearch(val results: List<Result>) {

    @Serializable
    data class Result(
        val id: Long,
        val name: String,
        val released: @Serializable(with = LocalDateSerializer::class) LocalDate?,
        @SerialName("background_image") val backgroundImage: String?,
        val platforms: List<RemotePlatform>,
        val playtime: Long
    )
}

@Serializable
data class RemoteGame(
    val id: Long,
    val name: String,
    @SerialName("description_raw") val description: String,
    @SerialName("released") val releaseDate: @Serializable(with = LocalDateSerializer::class) LocalDate?,
    @SerialName("background_image") val backgroundImage: String,
    val website: String,
    val playtime: Long,
    val platforms: List<RemotePlatform>
)

@Serializable
data class RemotePlatform(val platform: Item) {

    @Serializable
    data class Item(val id: Long, val name: String)
}

@Serializable
data class RemoteTrailers(val results: List<Result>) {

    @Serializable
    data class Result(
        val id: Long,
        val name: String,
        val preview: String,
        val data: Data
    ) {

        @Serializable
        data class Data(
            @SerialName("480") val quality480Url: String,
            @SerialName("max") val qualityMaxUrl: String
        )
    }
}