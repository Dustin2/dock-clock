package io.github.dustin2.dockclock.feature.appselection

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dustin2.dockclock.feature.settings.SettingsRepository
import java.text.Collator
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppSelectionUiState {
    data object Loading : AppSelectionUiState

    data class Ready(val apps: List<AppSelectionItem>) : AppSelectionUiState
}

data class AppSelectionItem(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap?,
    val isShown: Boolean
)

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    installedAppsSource: InstalledAppsSource,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    // Collator sorts by the user's language rules (accents, case) instead of raw code points.
    private val apps = flow {
        val collator = Collator.getInstance()
        emit(installedAppsSource.launchableApps().sortedWith(compareBy(collator) { it.label }))
    }

    val uiState: StateFlow<AppSelectionUiState> =
        combine(apps, settingsRepository.settings) { apps, settings ->
            AppSelectionUiState.Ready(
                apps.map { app ->
                    AppSelectionItem(
                        packageName = app.packageName,
                        label = app.label,
                        icon = app.icon,
                        isShown = app.packageName !in settings.hiddenApps
                    )
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            AppSelectionUiState.Loading
        )

    fun onAppShownChange(packageName: String, shown: Boolean) {
        viewModelScope.launch { settingsRepository.setAppHidden(packageName, hidden = !shown) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
