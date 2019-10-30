package at.florianschuster.playables

import at.florianschuster.control.test.TestCollector
import at.florianschuster.control.test.TestCoroutineScopeRule
import at.florianschuster.control.test.emission
import at.florianschuster.control.test.emissions
import at.florianschuster.control.test.expect
import at.florianschuster.control.test.test
import at.florianschuster.data.lce.Data
import at.florianschuster.playables.core.DataRepo
import at.florianschuster.playables.core.model.Game
import at.florianschuster.playables.playables.PlayablesController
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

    private val dataRepo = mockk<DataRepo>()
    private lateinit var sut: PlayablesController
    private lateinit var states: TestCollector<PlayablesController.State>

    private val mockedGames = ConflatedBroadcastChannel(mockedGamesList)

    @Before
    fun setup() {
        every { dataRepo.reloadMyGames() } answers { mockedGames.offer(mockedGamesList) }
        every { dataRepo.observeMyGames() } returns mockedGames.asFlow()
    }

    private fun givenControllerInitialized() {
        sut = PlayablesController(dataRepo = dataRepo).apply { scope = testScopeRule }
        states = sut.test()
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

        // todo
//        sut.action(PlayablesController.Action.SetGamePlayed(0, true))
//        advanceTimeBy(300)
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
            Game(it.toLong(), "name", "desc", null, "web", 0L, false, false)
        }
    }
}