package at.florianschuster.playables.core.model

import java.time.LocalDate

data class Game(
    val id: Long,

    val name: String,
    val image: String? = null,
    val description: String? = null,
    val website: String? = null,
    val releaseDate: LocalDate? = null,
    val trailers: List<Trailer> = emptyList(),
    val platforms: List<Platform> = emptyList(),

    val added: Boolean = false,
    val played: Boolean = false
) {
    data class Trailer(val id: Long, val previewImage: String, val videoUrl: String)
}