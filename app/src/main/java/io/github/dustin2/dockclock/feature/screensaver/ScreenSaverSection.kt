package io.github.dustin2.dockclock.feature.screensaver

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.dustin2.dockclock.R
import io.github.dustin2.dockclock.core.designsystem.DockClockTheme

@Composable
fun ScreenSaverSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    ScreenSaverContent(onOpenSettings = { context.openScreenSaverSettings() }, modifier = modifier)
}

@Composable
fun ScreenSaverContent(onOpenSettings: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.screen_saver_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(stringResource(R.string.screen_saver_description))
            Button(onClick = onOpenSettings) {
                Text(stringResource(R.string.screen_saver_open_settings))
            }
        }
    }
}

/** Some manufacturers remove the screen saver settings screen, so fall back to display settings. */
private fun Context.openScreenSaverSettings() {
    try {
        startActivity(Intent(Settings.ACTION_DREAM_SETTINGS))
    } catch (_: ActivityNotFoundException) {
        startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
    }
}

@Preview
@Composable
private fun ScreenSaverContentPreview() {
    DockClockTheme { ScreenSaverContent(onOpenSettings = {}) }
}
