package io.github.dustin2.dockclock.feature.notifications

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface NotificationRepository {
    /** Active notifications, newest first. Empty when notification access is off. */
    val notifications: StateFlow<List<ClockNotification>>

    fun replaceAll(notifications: List<ClockNotification>)

    fun clear()
}

@Singleton
class InMemoryNotificationRepository @Inject constructor() : NotificationRepository {
    private val state = MutableStateFlow(emptyList<ClockNotification>())

    override val notifications: StateFlow<List<ClockNotification>> = state.asStateFlow()

    override fun replaceAll(notifications: List<ClockNotification>) {
        state.value = notifications.sortedByDescending { it.postedAtMillis }
    }

    override fun clear() {
        state.value = emptyList()
    }
}
