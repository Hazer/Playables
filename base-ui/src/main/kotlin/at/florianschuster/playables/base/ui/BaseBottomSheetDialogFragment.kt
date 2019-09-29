package at.florianschuster.playables.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.leakcanary.RefWatcher
import org.koin.android.ext.android.inject

abstract class BaseBottomSheetDialogFragment(
    @LayoutRes protected val layout: Int
) : BottomSheetDialogFragment() {
    private val refWatcher: RefWatcher by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout, container, false)

    override fun onDestroy() {
        super.onDestroy()
        refWatcher.watch(this)
    }
}