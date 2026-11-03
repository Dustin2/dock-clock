package io.github.dustin2.dockclock.feature.notifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Bound by the system only while the user grants notification access, so it needs no
 * foreground service. It re-reads the full active list on every change instead of applying
 * deltas, which keeps the repository from drifting out of sync.
 */
@AndroidEntryPoint
class DockClockNotificationListener : NotificationListenerService() {
    @Inject
    lateinit var repository: NotificationRepository

    override fun onListenerConnected() = refresh()

    override fun onListenerDisconnected() = repository.clear()

    override fun onNotificationPosted(sbn: StatusBarNotification?) = refresh()

    override fun onNotificationRemoved(sbn: StatusBarNotification?) = refresh()

    private fun refresh() {
        val active =
            try {
                activeNotifications.orEmpty()
            } catch (_: SecurityException) {
                // Thrown when access was revoked between the callback and this call.
                emptyArray()
            }
        repository.replaceAll(
            active
                .filter { sbn ->
                    isShownOnClock(
                        packageName = sbn.packageName,
                        ownPackageName = packageName,
                        isOngoing = sbn.isOngoing,
                        isGroupSummary =
                        sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0
                    )
                }.map { it.toClockNotification() }
        )
    }
}

private fun StatusBarNotification.toClockNotification(): ClockNotification {
    val extras = notification.extras
    return ClockNotification(
        key = key,
        packageName = packageName,
        title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
        text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
        postedAtMillis = postTime
    )
}
