package at.florianschuster.playables.core.remote

import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDate

class MockedGamesApi {

    val api: RemoteGamesApi = mockk {
        remoteGames.forEach { game ->
            coEvery { game(game.id) } returns game
        }
        remoteSearches.forEach { page, search ->
            coEvery { search(any(), page) } returns search
        }
    }

    val remoteGames = (0L..5L).map {
        RemoteGame(
            it,
            "name $it",
            "description $it",
            LocalDate.MIN,
            "image: $it",
            "website: $it",
            100L,
            remotePlatforms
        )
    }

    val remoteSearchResults = (0L..5L).map {
        RemoteSearch.Result(
            it,
            "name: $it",
            LocalDate.MIN,
            "image: $it",
            remotePlatforms,
            0
        )
    }

    val remoteSearches = mapOf(
        1 to RemoteSearch(remoteSearchResults),
        2 to RemoteSearch(remoteSearchResults)
    )

    val remotePlatforms = listOf(
        RemotePlatform(RemotePlatform.Item(4, "PC")),
        RemotePlatform(RemotePlatform.Item(18, "Playstation")),
        RemotePlatform(RemotePlatform.Item(1, "XboxOne"))
    )
}