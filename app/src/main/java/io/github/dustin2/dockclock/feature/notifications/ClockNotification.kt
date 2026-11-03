package io.github.dustin2.dockclock.feature.notifications

/** A notification as shown on the clock. Only kept in memory, never persisted or logged. */
data class ClockNotification(
    val key: String,
    val packageName: String,
    val title: String?,
    val text: String?,
    val postedAtMillis: Long
)

/**
 * Ongoing notifications (media, calls, foreground services) are status, not news, and group
 * summaries would duplicate their children. Our own notifications are never interesting here.
 */
fun isShownOnClock(
    packageName: String,
    ownPackageName: String,
    isOngoing: Boolean,
    isGroupSummary: Boolean
): Boolean = !isOngoing && !isGroupSummary && packageName != ownPackageName
