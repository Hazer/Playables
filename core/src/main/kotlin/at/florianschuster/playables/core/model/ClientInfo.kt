package at.florianschuster.playables.core.model

data class ClientInfo(
    val appName: String,
    val debug: Boolean,
    val version: Version,
    val buildType: String,
    val flavor: String,
    val userAgent: String
) {
    data class Version(
        val code: Int,
        val name: String
    ) {
        override fun toString(): String = "$name-b$code"
    }
}
