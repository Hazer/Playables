package at.florianschuster.playables.info

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import at.florianschuster.control.bind
import at.florianschuster.playables.R
import at.florianschuster.playables.base.BaseBottomSheetDialogFragment
import at.florianschuster.playables.core.provider.AnalyticsProvider
import at.florianschuster.playables.util.openChromeTab
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.koin.android.ext.android.inject
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.checkedChanges

fun AppCompatActivity.showInfoBottomSheet(): InfoBottomSheetDialogFragment {
    return InfoBottomSheetDialogFragment().apply {
        show(supportFragmentManager, "InfoBottomSheet")
    }
}

class InfoBottomSheetDialogFragment : BaseBottomSheetDialogFragment(R.layout.fragment_info) {
    private val analyticsProvider: AnalyticsProvider by inject()

    override fun getTheme(): Int = R.style.Playables_BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    init {
        lifecycleScope.launchWhenStarted {
            flowOf(
                infoPrivacyPolicyButton.clicks().map { "https://florianschuster.at/playables/privacy.html" },
                infoProgrammedByButton.clicks().map { "https://florianschuster.at/" },
                infoRawgButton.clicks().map { "https://rawg.io/" }
            ).flattenMerge().bind { openChromeTab(it) }.launchIn(scope = this)

            analyticsCheckbox.isChecked = analyticsProvider.enabled

            analyticsCheckbox.checkedChanges()
                .bind(analyticsProvider::enabled::set)
                .launchIn(scope = this)
        }
    }
}