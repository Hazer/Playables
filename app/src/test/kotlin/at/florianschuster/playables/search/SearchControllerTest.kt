package at.florianschuster.playables.search

import at.florianschuster.playables.core.GamesRepo
import at.florianschuster.test.flow.TestCoroutineScopeRule
import at.florianschuster.test.flow.TestFlow
import at.florianschuster.test.flow.testIn
import io.mockk.mockk
import org.junit.Rule

internal class SearchControllerTest {

    @get:Rule
    val testScopeRule = TestCoroutineScopeRule()

    private lateinit var sut: SearchController
    private lateinit var states: TestFlow<SearchController.State>

    private val dataRepo = mockk<GamesRepo>()

    private fun givenControllerInitialized() {
        sut = SearchController(gamesRepo = dataRepo).apply { scope = testScopeRule }
        states = sut.state.testIn(testScopeRule)
    }



}