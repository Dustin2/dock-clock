package io.github.dustin2.dockclock.feature.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ClockUiState(val time: String, val date: String)

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val clock: Clock,
    private val settings: DeviceTimeSettings
) : ViewModel() {
    val uiState: StateFlow<ClockUiState> =
        minuteTicks()
            .map(::toUiState)
            // Stops ticking shortly after the screen is no longer visible.
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                toUiState(clock.instant())
            )

    /** Emits now, then right after each minute boundary, so the display never lags behind. */
    private fun minuteTicks(): Flow<Instant> = flow {
        while (true) {
            val now = clock.instant()
            emit(now)
            delay(MILLIS_PER_MINUTE - now.toEpochMilli() % MILLIS_PER_MINUTE)
        }
    }

    private fun toUiState(instant: Instant): ClockUiState {
        val locale = settings.locale()
        val dateTime = instant.atZone(settings.zone())
        val timePattern = if (settings.is24HourFormat()) "HH:mm" else "h:mm a"
        return ClockUiState(
            time = dateTime.format(DateTimeFormatter.ofPattern(timePattern, locale)),
            date = dateTime.format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
            )
        )
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
