package at.florianschuster.playables.playables

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val playablesModule = module {
    factory { PlayablesAdapter() }
    viewModel { PlayablesController(gamesRepo = get()) }
}