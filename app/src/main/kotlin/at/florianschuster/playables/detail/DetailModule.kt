package at.florianschuster.playables.detail

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val detailModule = module {
    viewModel { (id: Long) -> DetailController(gameId = id, gamesRepo = get()) }
}
