package io.github.dustin2.dockclock.feature.clock

import java.time.LocalTime

val NIGHT_START: LocalTime = LocalTime.parse("22:00")
val NIGHT_END: LocalTime = LocalTime.parse("07:00")

/** Start is inclusive and end exclusive; a range where start is after end crosses midnight. */
fun isNightTime(
    time: LocalTime,
    start: LocalTime = NIGHT_START,
    end: LocalTime = NIGHT_END
): Boolean = if (start <= end) {
    time >= start && time < end
} else {
    time >= start || time < end
}
