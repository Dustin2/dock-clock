package io.github.dustin2.dockclock.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    val uiState: StateFlow<UserSettings> =
        settingsRepository.settings
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                UserSettings()
            )

    fun onHideNotificationContentChange(hide: Boolean) {
        viewModelScope.launch { settingsRepository.setHideNotificationContent(hide) }
    }

    fun onNightModeChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNightMode(enabled) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
