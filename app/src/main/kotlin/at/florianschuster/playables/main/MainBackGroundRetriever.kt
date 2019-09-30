package at.florianschuster.playables.main

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.florianschuster.playables.util.cachedBlurredScreenshot
import java.io.File

interface MainBackGroundRetriever {
    val background: ViewGroup

    suspend fun retrieveBlurredScreenShot(): File = background.cachedBlurredScreenshot()
}

suspend fun Fragment.retrieveActivityBlurredScreenShot(): File? {
    val activity = requireActivity() as? MainBackGroundRetriever ?: return null
    return activity.retrieveBlurredScreenShot()
}