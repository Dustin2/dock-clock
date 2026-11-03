package io.github.dustin2.dockclock.feature.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dustin2.dockclock.core.designsystem.DockClockTheme

@Composable
fun ClockScreen(viewModel: ClockViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ClockContent(uiState)
}

@Composable
fun ClockContent(uiState: ClockUiState, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Auto-size so the time fills the width on any screen instead of a fixed font size.
        BasicText(
            text = uiState.time,
            // Leaves a margin so the time never touches the screen edges.
            modifier = Modifier.fillMaxWidth(TIME_WIDTH_FRACTION),
            style = MaterialTheme.typography.displayLarge.copy(
                color = colors.onBackground,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(maxFontSize = MAX_TIME_FONT_SIZE)
        )
        Text(
            text = uiState.date,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onSurfaceVariant
        )
    }
}

private val MAX_TIME_FONT_SIZE = 240.sp
private const val TIME_WIDTH_FRACTION = 0.85f

@Preview(widthDp = 800, heightDp = 360)
@Composable
private fun ClockContentPreview() {
    DockClockTheme {
        ClockContent(ClockUiState(time = "14:05", date = "Tuesday, November 3, 2026"))
    }
}
