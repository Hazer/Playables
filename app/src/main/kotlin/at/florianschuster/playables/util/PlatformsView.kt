package at.florianschuster.playables.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import at.florianschuster.playables.R
import at.florianschuster.playables.core.model.Platform

class PlatformsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    enum class Style(val params: LayoutParams) {
        Small(LayoutParams(12.px, 12.px).apply {
            marginStart = 1.px
            marginEnd = 1.px
        }),
        Default(LayoutParams(24.px, 24.px).apply {
            marginStart = 2.px
            marginEnd = 2.px
        })
    }

    init {
        orientation = HORIZONTAL
    }

    fun setPlatforms(platforms: List<Platform>, style: Style = Style.Default) {
        removeAllViews()

        platforms.forEach { platform ->
            val imageView = ImageView(context).apply {
                imageTintList = ColorStateList.valueOf(Color.WHITE)
                setImageResource(platform.asIcon())
            }
            addView(imageView, style.params)
        }
    }
}

@DrawableRes
private fun Platform.asIcon(): Int = when (this) {
    Platform.PC -> R.drawable.ic_windows
    Platform.MAC -> R.drawable.ic_apple
    Platform.Playstation -> R.drawable.ic_playstation
    Platform.XBox -> R.drawable.ic_xbox
    Platform.Switch -> R.drawable.ic_switch
    Platform.Phone -> R.drawable.ic_phone
    Platform.NintendoDS -> R.drawable.ic_nintendo_ds
    Platform.Web -> R.drawable.ic_web
    Platform.Other -> R.drawable.ic_devices_other
}