package at.florianschuster.playables.core

import at.florianschuster.playables.core.local.LocalGameData
import at.florianschuster.playables.core.local.MockedGamesDatabase
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.core.provider.TestDispatcherProvider
import at.florianschuster.playables.core.remote.MockedGamesApi
import at.florianschuster.test.flow.TestCoroutineScopeRule
import at.florianschuster.test.flow.testIn
import io.mockk.coVerify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GamesRepoTest {

    @get:Rule
    val testScopeRule = TestCoroutineScopeRule()

    private val mockedGamesApi = MockedGamesApi()
    private val mockedGamesDatabase = MockedGamesDatabase()

    private val sut = GamesRepo(
        mockedGamesApi.api,
        mockedGamesDatabase.db,
        TestDispatcherProvider
    )

    @Test
    fun `search successful`() = runBlockingTest {
        val query = "query"
        val page = 2
        val games = sut.search(query, page)

        coVerify(exactly = 1) { mockedGamesApi.api.search(query, page) }
        coVerify(exactly = mockedGamesApi.remoteSearches.getValue(page).results.count()) {
            mockedGamesDatabase.db.get(any())
        }

        // todo
    }

    @Test
    fun `search times out`() = runBlockingTest {
        val query = "query"
        val page = 2
        val games = sut.search(query, page)

        coVerify(exactly = 1) { mockedGamesApi.api.search(query, page) }
        coVerify(exactly = 0) { mockedGamesDatabase.db.get(any()) }
        // todo
    }

    @Test
    fun `add game`() = runBlockingTest {
        val mockedGameId = 32L
        val mockedGame = Game(mockedGameId, "some game", played = true)

        sut.add(mockedGame)

        coVerify(exactly = 1) { mockedGamesDatabase.db.insertOrUpdate(any()) }

        assertEquals(
            object : LocalGameData {
                override val gameId: Long = mockedGameId
                override val played: Boolean = true
            },
            mockedGamesDatabase.insertOrUpdateSlot.captured
        )
    }

    @Test
    fun `delete game`() = runBlockingTest {
        val gameId = 0L
        sut.remove(gameId)

        coVerify(exactly = 1) { mockedGamesDatabase.db.delete(gameId) }

        assertEquals(gameId, mockedGamesDatabase.deletedSlot.captured)
    }

    @Test
    fun `ReloadMyGames triggers flow`() {
        val gamesTestFlow = sut.observeMyGames().testIn(testScopeRule)

        sut.reloadMyGames()

        gamesTestFlow
        // todo assert content
    }

    @Test
    fun `set played`() = runBlockingTest {
        val gameId = 0L
        val played = false

        sut.setPlayed(gameId, played)

        coVerify(exactly = 1) { mockedGamesDatabase.db.insertOrUpdate(any()) }
        assertEquals(
            object : LocalGameData {
                override val gameId: Long = gameId
                override val played: Boolean = played
            },
            mockedGamesDatabase.insertOrUpdateSlot.captured
        )
    }
}