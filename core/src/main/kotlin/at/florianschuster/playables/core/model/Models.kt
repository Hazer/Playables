package at.florianschuster.playables.core.model

data class SearchResult(
    val id: Long,
    val name: String,
    val image: String? = null
)

data class Game(
    val id: Long,
    val name: String,
    val description: String,
    val image: String? = null,
    val website: String,
    val releaseDateInUnix: Long
)