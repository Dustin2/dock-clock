package io.github.dustin2.dockclock.feature.notifications

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShownOnClockTest {
    private fun shown(
        packageName: String = OTHER_APP,
        isOngoing: Boolean = false,
        isGroupSummary: Boolean = false
    ) = isShownOnClock(packageName, OWN_APP, isOngoing, isGroupSummary)

    @Test
    fun `shows regular notifications from other apps`() = assertTrue(shown())

    @Test
    fun `hides ongoing notifications`() = assertFalse(shown(isOngoing = true))

    @Test
    fun `hides group summaries`() = assertFalse(shown(isGroupSummary = true))

    @Test
    fun `hides own notifications`() = assertFalse(shown(packageName = OWN_APP))

    private companion object {
        const val OWN_APP = "io.github.dustin2.dockclock"
        const val OTHER_APP = "com.example.chat"
    }
}
