package at.florianschuster.playables.main

import android.view.View
import androidx.fragment.app.FragmentActivity
import at.florianschuster.playables.R
import com.tailoredapps.androidutil.ui.extensions.afterMeasured

interface MainHeaderChild {
    val parent: FragmentActivity

    fun afterMainHeaderMeasure(after: (header: View) -> Unit) {
        val activityHeader = parent.findViewById<View>(R.id.motionHeaderContainer)
        require(activityHeader != null) { "${this::class.java.simpleName} is not child of MainActivity" }
        activityHeader.afterMeasured { after(this) }
    }
}