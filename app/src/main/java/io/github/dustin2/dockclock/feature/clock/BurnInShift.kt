package io.github.dustin2.dockclock.feature.clock

/** How far the clock content is moved from its centered position, in dp. */
data class BurnInShift(val xDp: Int, val yDp: Int)

private const val MAX_SHIFT_DP = 6
private const val STEP_DP = 3

/**
 * A fixed 5x5 grid walked as a snake, so each step moves only a few pixels and every
 * position gets the same screen time. Random positions could repeat and wear pixels unevenly.
 */
private val positions: List<BurnInShift> =
    (-MAX_SHIFT_DP..MAX_SHIFT_DP step STEP_DP).flatMapIndexed { row, y ->
        val xs = (-MAX_SHIFT_DP..MAX_SHIFT_DP step STEP_DP).toList()
        (if (row % 2 == 0) xs else xs.reversed()).map { x -> BurnInShift(x, y) }
    }

/** OLED burn-in protection: a different position every minute, cycling through the grid. */
fun burnInShift(minute: Long): BurnInShift = positions[Math.floorMod(minute, positions.size)]

internal val burnInPositionCount: Int get() = positions.size
