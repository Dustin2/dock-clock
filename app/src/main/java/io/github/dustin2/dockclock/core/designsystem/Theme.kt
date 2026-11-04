package io.github.dustin2.dockclock.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/** App screens: wallpaper colors (Android 12+) that follow the system light or dark mode. */
@Composable
fun DockClockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = appColorScheme(darkTheme = isSystemInDarkTheme()),
        content = content
    )
}

/**
 * Clock and screen saver: always pure black, so OLED pixels stay off and it suits a dark room.
 * Wallpaper colors still tint details such as notification cards.
 */
@Composable
fun ClockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = appColorScheme(darkTheme = true)
            .copy(background = Color.Black, surface = Color.Black),
        content = content
    )
}

@Composable
private fun appColorScheme(darkTheme: Boolean): ColorScheme {
    // Wallpaper-based color only exists from Android 12.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        return if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    return if (darkTheme) darkColorScheme() else lightColorScheme()
}
