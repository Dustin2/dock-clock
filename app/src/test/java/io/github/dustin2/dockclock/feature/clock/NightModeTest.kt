package io.github.dustin2.dockclock.feature.clock

import java.time.LocalTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NightModeTest {
    @Test
    fun `night covers late evening and early morning`() {
        assertTrue(isNightTime(LocalTime.of(23, 30)))
        assertTrue(isNightTime(LocalTime.of(0, 0)))
        assertTrue(isNightTime(LocalTime.of(6, 59)))
    }

    @Test
    fun `day is not night`() {
        assertFalse(isNightTime(LocalTime.of(12, 0)))
        assertFalse(isNightTime(LocalTime.of(21, 59)))
    }

    @Test
    fun `start is inclusive and end exclusive`() {
        assertTrue(isNightTime(LocalTime.of(22, 0)))
        assertFalse(isNightTime(LocalTime.of(7, 0)))
    }

    @Test
    fun `range within the same day`() {
        val start = LocalTime.of(1, 0)
        val end = LocalTime.of(5, 0)

        assertTrue(isNightTime(LocalTime.of(3, 0), start, end))
        assertFalse(isNightTime(LocalTime.of(23, 0), start, end))
    }
}
