package at.florianschuster.playables.playables

import at.florianschuster.data.lce.Data
import at.florianschuster.playables.core.GamesRepo
import at.florianschuster.playables.core.model.Game
import at.florianschuster.test.flow.TestCoroutineScopeRule
import at.florianschuster.test.flow.TestFlow
import at.florianschuster.test.flow.emission
import at.florianschuster.test.flow.emissionCount
import at.florianschuster.test.flow.emissions
import at.florianschuster.test.flow.expect
import at.florianschuster.test.flow.testIn
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

internal class PlayablesControllerTest {

    @get:Rule
    val testScopeRule = TestCoroutineScopeRule()

    private lateinit var sut: PlayablesController
    private lateinit var states: TestFlow<PlayablesController.State>

    private val dataRepo = mockk<GamesRepo>()

    @Before
    fun setup() {
        val mockedGamesChannel = ConflatedBroadcastChannel(mockedGamesList)

        every { dataRepo.reloadMyGames() } answers { mockedGamesChannel.offer(mockedGamesList) }
        every { dataRepo.observeMyGames() } returns mockedGamesChannel.asFlow()
        coEvery { dataRepo.setPlayed(any(), any()) } answers {
            mockedGamesChannel.offer(
                mockedGamesList
            )
        }
    }

    private fun givenControllerInitialized() {
        sut = PlayablesController(gamesRepo = dataRepo).apply { scope = testScopeRule }
        states = sut.state.testIn(testScopeRule)
    }

    @Test
    fun `successful controller initialization`() = testScopeRule.runBlockingTest {
        givenControllerInitialized()
        advanceTimeBy(300)

        states expect emissions(
            PlayablesController.State(Data.Loading),
            PlayablesController.State(Data.Success(mockedGamesList))
        )
    }

    @Test
    fun `successful action ReloadGames`() = testScopeRule.runBlockingTest {
        givenControllerInitialized()
        advanceTimeBy(300)

        sut.action(PlayablesController.Action.ReloadGames)
        advanceTimeBy(300)

        states expect emission(2, PlayablesController.State(Data.Loading))
        states expect emission(3, PlayablesController.State(Data.Success(mockedGamesList)))
    }

    @Test
    fun `successful action SetGamePlayed`() = testScopeRule.runBlockingTest {
        givenControllerInitialized()
        advanceTimeBy(300)

        sut.action(PlayablesController.Action.SetGamePlayed(0L, false))

        states expect emissionCount(3)
    }

    @Test
    fun `failure on games load`() = testScopeRule.runBlockingTest {
        val exception = IOException()
        every { dataRepo.observeMyGames() } returns flow { throw exception }
        givenControllerInitialized()
        advanceTimeBy(300)

        states expect emissions(
            PlayablesController.State(Data.Loading),
            PlayablesController.State(Data.Failure(exception))
        )
    }

    companion object {
        private val mockedGamesList = (0..3).map {
            Game(
                id = it.toLong(),
                name = "name",
                added = false,
                played = false
            )
        }
    }
}