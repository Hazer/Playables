package at.florianschuster.playables.core.local

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot

class MockedGamesDatabase {
    val db: GamesDatabase = mockk {
        coEvery { insertOrUpdate(capture(insertOrUpdateSlot)) } just Runs
        coEvery { delete(capture(deletedSlot)) } just Runs
    }

    val insertOrUpdateSlot = slot<LocalGameData>()
    val deletedSlot = slot<Long>()
}