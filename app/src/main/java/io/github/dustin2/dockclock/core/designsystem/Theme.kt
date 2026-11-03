package io.github.dustin2.dockclock.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark only: the app is meant to sit on a desk with a black background, often at night.
private val DockClockColorScheme =
    darkColorScheme(
        background = Color.Black,
        surface = Color.Black
    )

@Composable
fun DockClockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DockClockColorScheme,
        content = content
    )
}
