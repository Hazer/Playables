package at.florianschuster.playables.info

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import at.florianschuster.playables.R
import at.florianschuster.playables.base.ui.BaseBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

fun AppCompatActivity.showInfoBottomSheet(): InfoBottomSheetDialogFragment {
    return InfoBottomSheetDialogFragment().apply {
        show(supportFragmentManager, "InfoBottomSheet")
    }
}

class InfoBottomSheetDialogFragment : BaseBottomSheetDialogFragment(R.layout.fragment_info) {
    override fun getTheme(): Int = R.style.Playables_BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    // todo
}