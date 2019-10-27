package at.florianschuster.playables.search

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val searchModule = module {
    viewModel { SearchController(dataRepo = get()) }
    factory { SearchAdapter() }
}