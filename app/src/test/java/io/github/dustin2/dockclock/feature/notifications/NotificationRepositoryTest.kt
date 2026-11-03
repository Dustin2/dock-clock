package io.github.dustin2.dockclock.feature.notifications

import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationRepositoryTest {
    private val repository = InMemoryNotificationRepository()

    @Test
    fun `starts empty`() {
        assertEquals(emptyList<ClockNotification>(), repository.notifications.value)
    }

    @Test
    fun `replaceAll keeps newest first`() {
        val older = notification(key = "a", postedAtMillis = 1)
        val newer = notification(key = "b", postedAtMillis = 2)

        repository.replaceAll(listOf(older, newer))

        assertEquals(listOf(newer, older), repository.notifications.value)
    }

    @Test
    fun `replaceAll drops notifications no longer active`() {
        repository.replaceAll(listOf(notification(key = "a"), notification(key = "b")))

        repository.replaceAll(listOf(notification(key = "b")))

        assertEquals(listOf("b"), repository.notifications.value.map { it.key })
    }

    @Test
    fun `clear removes everything`() {
        repository.replaceAll(listOf(notification(key = "a")))

        repository.clear()

        assertEquals(emptyList<ClockNotification>(), repository.notifications.value)
    }

    private fun notification(key: String, postedAtMillis: Long = 0) =
        ClockNotification(key, "com.example", "Title", "Text", postedAtMillis)
}
