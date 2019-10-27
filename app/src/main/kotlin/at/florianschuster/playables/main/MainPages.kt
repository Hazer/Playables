package at.florianschuster.playables.main

import androidx.fragment.app.Fragment
import at.florianschuster.playables.playables.PlayablesView
import at.florianschuster.playables.search.SearchView

enum class MainPages(val creator: () -> Fragment) {
    Playables({ PlayablesView() }),
    Search({ SearchView() })
}
