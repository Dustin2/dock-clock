package io.github.dustin2.dockclock.feature.clock

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.service.dreams.DreamService
import android.view.Window
import android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/**
 * Overrides the brightness of this window only, so no WRITE_SETTINGS permission is needed and
 * the system brightness comes back as soon as the clock is gone.
 */
@Composable
fun NightBrightnessEffect(isNight: Boolean) {
    val window = LocalView.current.context.findWindow() ?: return
    DisposableEffect(window, isNight) {
        window.setBrightness(if (isNight) NIGHT_BRIGHTNESS else BRIGHTNESS_OVERRIDE_NONE)
        onDispose { window.setBrightness(BRIGHTNESS_OVERRIDE_NONE) }
    }
}

private fun Window.setBrightness(brightness: Float) {
    attributes = attributes.apply { screenBrightness = brightness }
}

// The clock runs in an Activity or in the screen saver, and both own a window.
private tailrec fun Context.findWindow(): Window? = when (this) {
    is Activity -> window
    is DreamService -> window
    is ContextWrapper -> baseContext.findWindow()
    else -> null
}

// Not zero: some devices turn the backlight fully off at 0, which would hide the clock.
private const val NIGHT_BRIGHTNESS = 0.01f
