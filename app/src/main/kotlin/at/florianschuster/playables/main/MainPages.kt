package at.florianschuster.playables.main

import androidx.fragment.app.Fragment
import at.florianschuster.playables.playables.PlayablesFragment
import at.florianschuster.playables.search.SearchFragment

enum class MainPages(val creator: () -> Fragment) {
    Playables({ PlayablesFragment() }),
    Search({ SearchFragment() })
}
