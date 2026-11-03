package io.github.dustin2.dockclock.feature.permissions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PermissionsUiState(val hasNotificationAccess: Boolean)

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val notificationAccessChecker: NotificationAccessChecker
) : ViewModel() {
    private val state = MutableStateFlow(currentState())

    val uiState: StateFlow<PermissionsUiState> = state.asStateFlow()

    /** Android has no callback for access changes, and the user grants it in Settings, so re-check on return. */
    fun onResume() {
        state.value = currentState()
    }

    private fun currentState() =
        PermissionsUiState(hasNotificationAccess = notificationAccessChecker.isGranted())
}
