package at.florianschuster.playables.core.model

data class Game(
    val id: Long,

    val name: String,
    val description: String,
    val image: String? = null,
    val website: String? = null,
    val releaseDateInUnix: Long,

    val added: Boolean,
    val played: Boolean
)