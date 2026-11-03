package io.github.dustin2.dockclock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                DockClockApp(onStartClock = {
                    startActivity(Intent(this, ClockActivity::class.java))
                })
            }
        }
    }
}
