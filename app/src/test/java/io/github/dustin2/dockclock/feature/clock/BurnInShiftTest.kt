package io.github.dustin2.dockclock.feature.clock

import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BurnInShiftTest {
    private val cycle = (0L until burnInPositionCount).map(::burnInShift)

    @Test
    fun `stays within 6dp on both axes`() {
        assertTrue(cycle.all { abs(it.xDp) <= 6 && abs(it.yDp) <= 6 })
    }

    @Test
    fun `visits every position once per cycle`() {
        assertEquals(25, cycle.toSet().size)
    }

    @Test
    fun `moves at most one small step between consecutive minutes`() {
        cycle.zipWithNext().forEach { (a, b) ->
            assertNotEquals(a, b)
            assertTrue(abs(a.xDp - b.xDp) + abs(a.yDp - b.yDp) <= 3)
        }
    }

    @Test
    fun `repeats after a full cycle, including for very large minute values`() {
        val minute = 29_384_211L
        assertEquals(burnInShift(minute), burnInShift(minute + burnInPositionCount))
    }
}
