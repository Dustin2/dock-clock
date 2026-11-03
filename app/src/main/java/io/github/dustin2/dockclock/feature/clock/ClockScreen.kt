package io.github.dustin2.dockclock.feature.clock

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dustin2.dockclock.core.designsystem.DockClockTheme

@Composable
fun ClockScreen(viewModel: ClockViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NightBrightnessEffect(uiState.isNight)
    ClockContent(uiState)
}

@Composable
fun ClockContent(uiState: ClockUiState, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            // Applied after the background so only the content moves, never the black fill.
            .offset(x = uiState.burnInShift.xDp.dp, y = uiState.burnInShift.yDp.dp)
            .alpha(if (uiState.isNight) NIGHT_CONTENT_ALPHA else 1f)
    ) {
        // The screen saver cannot lock orientation, so portrait stacks instead of side by side.
        if (maxWidth > maxHeight) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                ClockFace(uiState.time, uiState.date, Modifier.weight(CLOCK_WEIGHT))
                if (uiState.notifications.isNotEmpty()) {
                    NotificationList(
                        notifications = uiState.notifications,
                        modifier = Modifier
                            .weight(NOTIFICATIONS_WEIGHT)
                            .fillMaxHeight()
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                ClockFace(uiState.time, uiState.date, Modifier.fillMaxWidth())
                if (uiState.notifications.isNotEmpty()) {
                    NotificationList(
                        notifications = uiState.notifications,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockFace(time: String, date: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Auto-size so the time fills the width on any screen instead of a fixed font size.
        BasicText(
            text = time,
            // Leaves a margin so the time never touches the screen edges.
            modifier = Modifier.fillMaxWidth(TIME_WIDTH_FRACTION),
            style = MaterialTheme.typography.displayLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(maxFontSize = MAX_TIME_FONT_SIZE)
        )
        Text(
            text = date,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NotificationList(
    notifications: List<NotificationUiModel>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        items(notifications, key = { it.key }) { NotificationItem(it) }
    }
}

@Composable
fun NotificationItem(notification: NotificationUiModel, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val iconModifier = Modifier.size(32.dp)
            val icon = notification.appIcon
            // The app name is shown next to the icon, so the icon itself is decorative.
            if (icon !=
                null
            ) {
                Image(icon, contentDescription = null, modifier = iconModifier)
            } else {
                Spacer(iconModifier)
            }
            Column {
                Text(
                    text = notification.appName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                notification.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                notification.text?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private val MAX_TIME_FONT_SIZE = 240.sp
private const val TIME_WIDTH_FRACTION = 0.85f
private const val CLOCK_WEIGHT = 3f
private const val NOTIFICATIONS_WEIGHT = 2f
private const val NIGHT_CONTENT_ALPHA = 0.4f

private val previewNotifications =
    listOf(
        NotificationUiModel("1", "Messages", null, "Ana", "Are we still on for lunch tomorrow?"),
        NotificationUiModel("2", "Calendar", null, "Team sync", "10:00 – 10:30")
    )

@Preview(widthDp = 800, heightDp = 360)
@Composable
private fun ClockContentPreview() {
    DockClockTheme {
        ClockContent(ClockUiState(time = "14:05", date = "Tuesday, November 3, 2026"))
    }
}

@Preview(widthDp = 800, heightDp = 360)
@Composable
private fun ClockContentWithNotificationsPreview() {
    DockClockTheme {
        ClockContent(ClockUiState("14:05", "Tuesday, November 3, 2026", previewNotifications))
    }
}

@Preview(widthDp = 800, heightDp = 360)
@Composable
private fun ClockContentNightPreview() {
    DockClockTheme {
        ClockContent(
            ClockUiState("23:05", "Tuesday, November 3, 2026", previewNotifications, isNight = true)
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun ClockContentPortraitPreview() {
    DockClockTheme {
        ClockContent(ClockUiState("14:05", "Tuesday, November 3, 2026", previewNotifications))
    }
}

@Preview(widthDp = 320)
@Composable
private fun NotificationItemPreview() {
    DockClockTheme { NotificationItem(previewNotifications.first()) }
}
