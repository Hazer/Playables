package at.florianschuster.playables.playables

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseFragment
import at.florianschuster.playables.controller.Controller
import at.florianschuster.playables.controller.ControllerViewModel
import at.florianschuster.playables.controller.DefaultController
import at.florianschuster.playables.core.DataRepo
import kotlinx.android.synthetic.main.fragment_playables.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class PlayablesFragment : BaseFragment(R.layout.fragment_playables) {
    init {
        lifecycleScope.launchWhenStarted {
            playablesRecyclerView.isVisible = false
            layoutPlayablesEmpty.isVisible = true
        }
    }
}

//class PlayablesController(
//    dataRepo: DataRepo
//) : ControllerViewModel<>() {
//
//}