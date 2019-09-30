package at.florianschuster.playables.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.api.get
import coil.transform.BlurTransformation
import java.io.File
import java.io.FileOutputStream

suspend fun View.cachedBlurredScreenshot(): File {
    val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)

    val blurredBitmap = Coil.get(bitmap) {
        transformations(BlurTransformation(context, 20f, 5f))
    }.toBitmap()

    val file = File(context.cacheDir, "screenshot_cached_file.jpg")
    FileOutputStream(file).use {
        blurredBitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
    }

    return file
}