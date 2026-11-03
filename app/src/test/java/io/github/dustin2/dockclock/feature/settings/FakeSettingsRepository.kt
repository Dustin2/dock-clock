package io.github.dustin2.dockclock.feature.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeSettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {
    private val state = MutableStateFlow(initial)

    override val settings: StateFlow<UserSettings> = state

    override suspend fun setHideNotificationContent(hide: Boolean) {
        state.value = state.value.copy(hideNotificationContent = hide)
    }
}
