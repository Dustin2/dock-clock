package io.github.dustin2.dockclock.feature.clock

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dustin2.dockclock.feature.notifications.AppInfoProvider
import io.github.dustin2.dockclock.feature.notifications.ClockNotification
import io.github.dustin2.dockclock.feature.notifications.NotificationRepository
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

data class ClockUiState(
    val time: String,
    val date: String,
    val notifications: List<NotificationUiModel> = emptyList()
)

data class NotificationUiModel(
    val key: String,
    val appName: String,
    val appIcon: ImageBitmap?,
    val title: String?,
    val text: String?
)

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val clock: Clock,
    private val settings: DeviceTimeSettings,
    notificationRepository: NotificationRepository,
    private val appInfoProvider: AppInfoProvider
) : ViewModel() {
    val uiState: StateFlow<ClockUiState> =
        combine(minuteTicks(), notificationRepository.notifications, ::toUiState)
            // Stops ticking shortly after the screen is no longer visible.
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                toUiState(clock.instant(), notificationRepository.notifications.value)
            )

    /** Emits now, then right after each minute boundary, so the display never lags behind. */
    private fun minuteTicks(): Flow<Instant> = flow {
        while (true) {
            val now = clock.instant()
            emit(now)
            delay(MILLIS_PER_MINUTE - now.toEpochMilli() % MILLIS_PER_MINUTE)
        }
    }

    // shortcut: app info is loaded on the main thread the first time a package shows up;
    // move it to a background dispatcher if icon loading ever causes jank.
    private fun toUiState(instant: Instant, notifications: List<ClockNotification>): ClockUiState {
        val locale = settings.locale()
        val dateTime = instant.atZone(settings.zone())
        val timePattern = if (settings.is24HourFormat()) "HH:mm" else "h:mm a"
        return ClockUiState(
            time = dateTime.format(DateTimeFormatter.ofPattern(timePattern, locale)),
            date = dateTime.format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
            ),
            notifications = notifications.map(::toUiModel)
        )
    }

    private fun toUiModel(notification: ClockNotification): NotificationUiModel {
        val appInfo = appInfoProvider.appInfo(notification.packageName)
        return NotificationUiModel(
            key = notification.key,
            appName = appInfo.label,
            appIcon = appInfo.icon,
            title = notification.title,
            text = notification.text
        )
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
