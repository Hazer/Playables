package at.florianschuster.playables.core.local

import at.florianschuster.playables.core.model.Platform

interface LocalGameData {
    val gameId: Long
    val name: String
    val imageUrl: String?
    val platforms: List<Platform>
    val played: Boolean
}