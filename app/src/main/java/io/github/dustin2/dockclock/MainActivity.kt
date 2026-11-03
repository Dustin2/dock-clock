package io.github.dustin2.dockclock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import io.github.dustin2.dockclock.core.designsystem.DockClockTheme
import io.github.dustin2.dockclock.feature.clock.ClockActivity

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DockClockTheme {
                HomeScreen(onStartClock = {
                    startActivity(Intent(this, ClockActivity::class.java))
                })
            }
        }
    }
}

@Composable
private fun HomeScreen(onStartClock: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Button(onClick = onStartClock) {
                Text(stringResource(R.string.start_clock))
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    DockClockTheme { HomeScreen(onStartClock = {}) }
}
