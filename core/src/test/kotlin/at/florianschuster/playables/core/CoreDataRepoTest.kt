package at.florianschuster.playables.core

import at.florianschuster.playables.core.local.GamesDatabase
import at.florianschuster.playables.core.local.LocalGameData
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.core.provider.TestDispatcherProvider
import at.florianschuster.playables.core.remote.RawgApi
import at.florianschuster.playables.core.remote.RemoteGame
import at.florianschuster.playables.core.remote.RemoteSearch
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CoreDataRepoTest {

    private val api = mockk<RawgApi>()
    private val database = mockk<GamesDatabase>()

    private val sut = CoreDataRepo(api, database, TestDispatcherProvider)

    private val databaseInsertSlot = slot<LocalGameData>()
    private val databaseDeletedSlot = slot<Long>()

    @Before
    fun setup() {
        coEvery { api.game(any()) } returns mockedRemoteGames.first()
        coEvery { api.search(any(), any()) } returns mockedSearchGames

        coEvery { database.insertOrUpdate(capture(databaseInsertSlot)) } just Runs
        coEvery { database.delete(capture(databaseDeletedSlot)) } just Runs
    }

    @Test
    fun `get successful`() = runBlockingTest {
        val gameId = 0L
        val game = sut.get(gameId)

        coVerify(exactly = 1) { api.game(gameId) }
        coVerify(exactly = 1) { database.get(gameId) }

        // todo
    }

    @Test
    fun `get times out`() = runBlockingTest {
        val gameId = 0L
        val game = sut.get(gameId)

        coVerify(exactly = 1) { api.game(gameId) }
        coVerify(exactly = 0) { database.get(gameId) }

        // todo
    }

    @Test
    fun `search successful`() = runBlockingTest {
        val query = "query"
        val page = 2
        val games = sut.search(query, page)

        coVerify(exactly = 1) { api.search(query, page) }
        coVerify(exactly = mockedSearchGames.results.count()) { database.get(any()) }

        // todo
    }

    @Test
    fun `search times out`() = runBlockingTest {
        val query = "query"
        val page = 2
        val games = sut.search(query, page)

        coVerify(exactly = 1) { api.search(query, page) }
        coVerify(exactly = 0) { database.get(any()) }
        // todo
    }

    @Test
    fun `add game`() = runBlockingTest {
        sut.add(mockedGame)

        coVerify(exactly = 1) { database.insertOrUpdate(any()) }

        assertEquals(
            object : LocalGameData {
                override val gameId: Long = 0L
                override val played: Boolean = false
            },
            databaseInsertSlot.captured
        )
    }

    @Test
    fun `delete game`() = runBlockingTest {
        val gameId = 0L
        sut.delete(gameId)

        coVerify(exactly = 1) { database.delete(gameId) }

        assertEquals(gameId, databaseDeletedSlot.captured)
    }

    @Test
    fun `ReloadMyGames triggers flow`() = runBlockingTest {
        val games = sut.observeMyGames().toList()

        sut.reloadMyGames()

        assertEquals(2, games.count())
        // todo assert content
    }

    @Test
    fun `set played`() = runBlockingTest {
        val gameId = 0L
        val played = false

        sut.setPlayed(gameId, played)

        coVerify(exactly = 1) { database.insertOrUpdate(any()) }
        assertEquals(
            object : LocalGameData {
                override val gameId: Long = gameId
                override val played: Boolean = played
            },
            databaseInsertSlot.captured
        )
    }

    companion object {
        private val mockedRemoteGames = (0L..5L).map {
            RemoteGame(
                it,
                "name $it",


                )
        }

        private val mockedSearchGames = RemoteSearch(
            5, null, null, (0L..5L).map {
                RemoteSearch.Result(it, "name: $it", it)
            }
        )

        private val mockedGame = Game(added = true, played = false)
    }
}