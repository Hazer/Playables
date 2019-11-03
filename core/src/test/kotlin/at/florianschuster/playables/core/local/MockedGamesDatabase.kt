package at.florianschuster.playables.core.local

import at.florianschuster.playables.core.model.Platform
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot

class MockedGamesDatabase {
    val insertOrUpdateSlot = slot<LocalGameData>()
    val updateSlotId = slot<Long>()
    val updateSlotPlayed = slot<Boolean>()
    val deletedSlot = slot<Long>()

    val mockedLocalGameData = object : LocalGameData {
        override val gameId: Long = 0L
        override val name: String = "name"
        override val imageUrl: String? = null
        override val platforms: List<Platform> = emptyList()
        override val played: Boolean = false
    }

    val db: GamesDatabase = mockk {
        coEvery { insertOrUpdate(capture(insertOrUpdateSlot)) } just Runs
        coEvery { update(capture(updateSlotId), capture(updateSlotPlayed)) } just Runs
//        every { observe() }
        coEvery { this@mockk.get(any()) } returns mockedLocalGameData
//        every { observeAll() }
//        coEvery { getAll() } returns
        coEvery { delete(capture(deletedSlot)) } just Runs
    }
}